package com.devrun.youtube;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.media.MediaHttpUploader;
import com.google.api.client.googleapis.media.MediaHttpUploaderProgressListener;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.JsonParser;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTubeScopes;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;
import com.google.api.services.youtube.model.VideoSnippet;
import com.google.api.services.youtube.model.VideoStatus;

/**
 * OAuth2를 사용하여 YouTube Data API(V3)를 통해 사용자 계정으로 비디오를 업로드하는 데모.
 *
 * TODO: 주의: 이 응용 프로그램으로 파일을 업로드하려면 비디오 파일을 프로젝트 폴더에 추가해야합니다!
 */
@Component
public class YouTubeUploader {
	
	private static final String CLIENT_SECRETS_FILE = "client_secrets.json";
	private static final String REDIRECT_URI = "https://devrun.site/lectureregitest"; // 이 부분을 본인의 리디렉션 URI로 수정하세요.
	private static final String VIDEO_FILE_FORMAT = "video/*";
	private static final String APPLICATION_NAME = "youtube-cmdline-uploadvideo-sample";

	private final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
	private HttpTransport httpTransport;

	public YouTubeUploader() throws Exception {
		httpTransport = GoogleNetHttpTransport.newTrustedTransport();
	}

	public VideoDto uploadVideo(VideoDto videoDto, HttpServletResponse httpServletResponse, String accessToken)
			throws IOException {
		try {
			MultipartFile videoFile = videoDto.getVideofile();

			Credential credential = new GoogleCredential().setAccessToken(accessToken);

			// YouTube 객체 초기화
			YouTube youtube = new YouTube.Builder(httpTransport, JSON_FACTORY, credential)
					.setApplicationName(APPLICATION_NAME).build();

			// 업로드할 비디오 파일 정보 출력
			System.out.println("업로드할 비디오 파일: " + videoFile.getOriginalFilename() + "을 선택했습니다.");

			// 업로드 전에 비디오에 추가 정보를 설정합니다.
			Video videoObjectDefiningMetadata = new Video();

			// 비디오를 공개로 설정하여 모든 사람이 볼 수 있도록합니다(대부분의 사람들이 원하는 것).
			// 이것이 실제로 기본값이지만 "비공개" 또는 "비공개"로 설정해야하는 경우를 위해 API를 통해 설정하는 방법을 보여주기 위해 사용했습니다.
			VideoStatus status = new VideoStatus();
			status.setPrivacyStatus("unlisted");
			videoObjectDefiningMetadata.setStatus(status);

			// 비디오 스니펫 객체를 사용하여 대부분의 메타데이터를 설정합니다.
			VideoSnippet snippet = new VideoSnippet();

			// Calendar 인스턴스는 테스트 목적으로 고유한 이름과 설명을 만들기 위해 사용되며,
			// 여러 파일이 업로드되는 것을 볼 수 있도록 합니다. 프로젝트에서 이것을 제거하고
			// 자체 표준 이름을 사용해야합니다.
			Calendar cal = Calendar.getInstance();
			snippet.setTitle("테스트 업로드: " + cal.getTime() + "에 Java를 사용하여 업로드됨");
			snippet.setDescription("YouTube Data API V3를 사용하여 " + cal.getTime() + "에 Java 라이브러리를 사용하여 업로드된 비디오");

			// 키워드 설정.
			List<String> tags = new ArrayList<String>();
			tags.add("테스트");
			tags.add("예제");
			tags.add("자바");
			tags.add("YouTube Data API V3");
			tags.add("지워주세요");
			snippet.setTags(tags);

			// 완성된 스니펫을 비디오 객체에 설정합니다.
			videoObjectDefiningMetadata.setSnippet(snippet);

			InputStreamContent mediaContent = new InputStreamContent(VIDEO_FILE_FORMAT, videoFile.getInputStream());
			mediaContent.setLength(videoFile.getSize());

			// 업로드 명령에는 다음이 포함됩니다: 파일이 성공적으로 업로드된 후 반환되는 정보, 업로드된 비디오와 연결되는 메타데이터, 비디오 파일
			// 자체.
			YouTube.Videos.Insert videoInsert = youtube.videos().insert("snippet,statistics,status",
					videoObjectDefiningMetadata, mediaContent);

			// 업로드 유형을 설정하고 이벤트 리스너를 추가합니다.
			MediaHttpUploader uploader = videoInsert.getMediaHttpUploader();

			/*
			 * 직접 미디어 업로드가 활성화되었는지 여부를 설정합니다. True = 전체 미디어 콘텐츠를 한 번의 요청으로 업로드합니다. False
			 * (기본값) = 데이터 청크로 업로드하기 위한 재개 가능한 미디어 업로드 프로토콜을 사용합니다.
			 */
			uploader.setDirectUploadEnabled(false);

			MediaHttpUploaderProgressListener progressListener = new MediaHttpUploaderProgressListener() {
				public void progressChanged(MediaHttpUploader uploader) throws IOException {
					switch (uploader.getUploadState()) {
					case INITIATION_STARTED:
						System.out.println("시작 초기화");
						break;
					case INITIATION_COMPLETE:
						System.out.println("초기화 완료");
						break;
					case MEDIA_IN_PROGRESS:
						System.out.println("진행 중: " + uploader.getProgress());
						break;
					case MEDIA_COMPLETE:
						System.out.println("완료!");
						break;
					case NOT_STARTED:
						System.out.println("시작되지 않음");
						break;
					}
				}
			};
			uploader.setProgressListener(progressListener);

			// 업로드 실행.
			Video returnedVideo = videoInsert.execute();

			// 반환된 결과 출력.
			System.out.println("\n================== 반환된 동영상 ==================\n");
			System.out.println("  - Id: " + returnedVideo.getId());
			System.out.println("  - 제목: " + returnedVideo.getSnippet().getTitle());
			System.out.println("  - 태그: " + returnedVideo.getSnippet().getTags());
			System.out.println("  - 개인 정보 설정: " + returnedVideo.getStatus().getPrivacyStatus());
			System.out.println("  - 동영상 조회수: " + returnedVideo.getStatistics().getViewCount());

			// 업로드된 비디오의 정보를 VideoInfo 객체로 생성하여 반환
			String videoId = returnedVideo.getId();
			System.out.println("--------------------------------비디오 정보 받아오기----------------------------------------------");
//			YouTube.Videos.List request = youtube.videos()
//		            .list("snippet,contentDetails,statistics");
//			System.out.println("--------------------------------비디오 정보 받아오기 1----------------------------------------------");
//		    VideoListResponse response = request.setId(videoId).execute();
//		    System.out.println("--------------------------------비디오 정보 받아오기 2----------------------------------------------");		        
			String videoUrl = "https://www.youtube.com/watch?v=" + videoId;
			videoDto.setVideoId(videoId);
			videoDto.setVideoLink(videoUrl);
			

//			String videoTotalPlayTime = response.getItems().get(0).getContentDetails().getDuration();
//			System.out.println("--------------------------------비디오 정보 받아오기 3----------------------------------------------");
//			String period = videoTotalPlayTime;
//			Duration duration = Duration.parse(period);
//			System.out.println("--------------------------------비디오 정보 받아오기 4----------------------------------------------");
//			int totalSeconds = (int) duration.getSeconds();

//			videoDto.setTotalPlayTime(totalSeconds);
//			System.out.println("totalSeconds : " + totalSeconds);
			System.out.println("videoUrl : " + videoUrl);
			System.out.println("--------------------------------비디오 정보 받아오기 완료----------------------------------------------");
			return videoDto;
		} catch (IOException e) {
			throw new IOException("비디오 업로드 중 오류가 발생했습니다.", e);
		}
	}

//    private Credential authorize(String accessToken) throws IOException {
//        GoogleClientSecrets clientSecrets = loadClientSecrets();
//        
//        // GoogleTokenResponse를 사용하여 엑세스 토큰을 교환합니다.
//        GoogleTokenResponse tokenResponse = new GoogleTokenResponse();
//        tokenResponse.setAccessToken(accessToken);
//        
//        // 토큰 응답을 사용하여 Credential을 만듭니다.
//        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
//                httpTransport, JSON_FACTORY, clientSecrets,
//                Collections.singleton(YouTubeScopes.YOUTUBE_UPLOAD))
//                .setAccessType("offline")
//                .build();
//        
//        return flow.createAndStoreCredential(tokenResponse, null);
//    }

	// 엑세스 토큰을 사용하여 GoogleCredential을 생성합니다.
//    private GoogleCredential createCredential(String accessToken) {
//        return new GoogleCredential().setAccessToken(accessToken);
//    }
//    
//    private GoogleClientSecrets loadClientSecrets() throws IOException {
//        InputStream clientSecretsStream = getClass().getClassLoader().getResourceAsStream(CLIENT_SECRETS_FILE);
//        if (clientSecretsStream == null) {
//            throw new IOException("클라이언트 비밀 파일을 찾을 수 없습니다.");
//        }
//
//        Charset charset = StandardCharsets.UTF_8; // 사용할 문자 집합을 지정
//        JsonObjectParser parser = new JsonObjectParser(JSON_FACTORY);
//        return parser.parseAndClose(clientSecretsStream, charset, GoogleClientSecrets.class);
//    }

}
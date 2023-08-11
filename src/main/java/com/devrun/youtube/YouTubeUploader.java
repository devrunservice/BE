package com.devrun.youtube;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.StoredCredential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.media.MediaHttpUploader;
import com.google.api.client.googleapis.media.MediaHttpUploaderProgressListener;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.DataStore;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoSnippet;
import com.google.api.services.youtube.model.VideoStatus;


/**
 * OAuth2를 사용하여 YouTube Data API(V3)를 통해 사용자 계정으로 비디오를 업로드하는 데모.
 *
 *  TODO: 주의: 이 응용 프로그램으로 파일을 업로드하려면 비디오 파일을 프로젝트 폴더에 추가해야합니다!
 *
 * @author Jeremy Walker
 */
@Component
public class YouTubeUploader {

	
	
	
  /** HTTP 전송을 위한 전역 인스턴스. */
  private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();

  /** JSON 생성을 위한 전역 인스턴스. */
  private static final JsonFactory JSON_FACTORY = new GsonFactory();

  /** 모든 API 요청을 수행하는 데 사용되는 YouTube 전역 인스턴스. */
  private static YouTube youtube;

  /* 업로드되는 비디오의 형식(MIME 유형)을 나타내는 전역 인스턴스. */
  private static String VIDEO_FILE_FORMAT = "video/*";

  /*public YouTubeUploader() {
      // YouTube 객체 초기화
      try {
          Credential credential = authorize(Collections.singletonList("https://www.googleapis.com/auth/youtube.upload"));
          youtube = new YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                  .setApplicationName("Devrun").build();
      } catch (Exception e) {
          e.printStackTrace();
      }
  }*/

 
  
  /**
   * 사용자의 보호된 데이터에 액세스할 수 있도록 설치된 응용 프로그램에 권한 부여.
   *
   * @param scopes YouTube 업로드를 실행하는 데 필요한 스코프 목록.
   */
  private static Credential authorize(List<String> scopes) throws Exception {

    // 클라이언트 비밀을 로드합니다.
	  ClassPathResource resource = new ClassPathResource("client_secrets.json");
	  InputStream inputStream = resource.getInputStream();
	  GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(inputStream));



    // 기본값이 대체되었는지 확인합니다(Default = "Enter X here").
    if (clientSecrets.getDetails().getClientId().startsWith("Enter")
        || clientSecrets.getDetails().getClientSecret().startsWith("Enter ")) {
      System.out.println(
          "https://console.developers.google.com/project/_/apiui/credential의 "
          + "Client ID 및 Secret을 youtube-cmdline-uploadvideo-sample/src/main/resources/client_secrets.json"
          + "에 입력하세요.");
      System.exit(1);
    }

    // 파일 자격 증명 저장소를 설정합니다.
    DataStore<StoredCredential> credentialStore = new FileDataStoreFactory(new File(System.getProperty("user.home"), ".credentials"))
            .getDataStore("youtube_uploadvideo");


    // 승인 코드 흐름을 설정합니다.
    GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
    	    HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, scopes)
    	    .setCredentialDataStore(credentialStore)
    	    .build();

    // 로컬 서버를 빌드하고 포트 8080에 바인딩합니다.
    LocalServerReceiver localReceiver = new LocalServerReceiver.Builder().setPort(8080).build();

    // 승인합니다.
    return new AuthorizationCodeInstalledApp(flow, localReceiver).authorize("user");
  }

  

  /**
   * 사용자의 YouTube 계정으로 비디오를 업로드합니다.
   *
   * @param videoFile 업로드할 비디오 파일
   * @return 업로드된 비디오의 ID
 * @throws Exception 
   */
  public VideoInfo uploadVideo(MultipartFile videoFile, HttpServletResponse response) throws Exception {
	    // YouTube 업로드에 필요한 스코프.
	    List<String> scopes = Collections.singletonList("https://www.googleapis.com/auth/youtube.upload");

	    try {
	        // 권한 부여.
	        Credential credential = authorize(scopes);
	        
	        // 모든 API 요청을 수행하는 YouTube 객체.
	        youtube = new YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
	                .setApplicationName("youtube-cmdline-uploadvideo-sample").build();

	        // 업로드할 비디오 파일 정보 출력.
	        System.out.println("업로드할 비디오 파일: " + videoFile.getOriginalFilename() + "을 선택했습니다.");


	        // 업로드 전에 비디오에 추가 정보를 설정합니다.
	        Video videoObjectDefiningMetadata = new Video();
	       
	        /*
	         * 비디오를 공개로 설정하여 모든 사람이 볼 수 있도록합니다(대부분의 사람들이 원하는 것).
	         * 이것이 실제로 기본값이지만 "비공개" 또는 "비공개"로 설정해야하는 경우를 위해 API를 통해 설정하는 방법을 보여주기 위해 사용했습니다.
	         */
	        VideoStatus status = new VideoStatus();
	        status.setPrivacyStatus("private");
	        videoObjectDefiningMetadata.setStatus(status);

	        // 비디오 스니펫 객체를 사용하여 대부분의 메타데이터를 설정합니다.
	        VideoSnippet snippet = new VideoSnippet();

	        /*
	         * Calendar 인스턴스는 테스트 목적으로 고유한 이름과 설명을 만들기 위해 사용되며,
	         * 여러 파일이 업로드되는 것을 볼 수 있도록 합니다. 프로젝트에서 이것을 제거하고
	         * 자체 표준 이름을 사용해야합니다.
	         */
	        Calendar cal = Calendar.getInstance();
	        snippet.setTitle("테스트 업로드: " + cal.getTime() + "에 Java를 사용하여 업로드됨");
	        snippet.setDescription(
	            "YouTube Data API V3를 사용하여 " + cal.getTime() + "에 Java 라이브러리를 사용하여 업로드된 비디오");

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

	        InputStreamContent mediaContent = new InputStreamContent(
	                VIDEO_FILE_FORMAT, videoFile.getInputStream());
	            mediaContent.setLength(videoFile.getSize());

	        /*
	         * 업로드 명령에는 다음이 포함됩니다: 1. 파일이 성공적으로 업로드된 후 반환되는 정보.
	         * 2. 업로드된 비디오와 연결되는 메타데이터. 3. 비디오 파일 자체.
	         */

	        YouTube.Videos.Insert videoInsert = youtube.videos()
	                .insert("snippet,statistics,status", videoObjectDefiningMetadata, mediaContent);

	        // 업로드 유형을 설정하고 이벤트 리스너를 추가합니다.
	          MediaHttpUploader uploader = videoInsert.getMediaHttpUploader();

	          /*
	           * 직접 미디어 업로드가 활성화되었는지 여부를 설정합니다. True = 전체 미디어 콘텐츠를 한 번의 요청으로 업로드합니다. False (기본값) = 데이터 청크로 업로드하기 위한 재개 가능한 미디어 업로드 프로토콜을 사용합니다.
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
	                  System.out.println("업로드 진행 중");
	                  System.out.println("업로드 퍼센트: " + uploader.getProgress());
	                  break;
	                case MEDIA_COMPLETE:
	                  System.out.println("업로드 완료!");
	                  break;
	                case NOT_STARTED:
	                  System.out.println("업로드 시작되지 않음!");
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
	        String videoUrl = "https://www.youtube.com/watch?v="+videoId;
	        
	        
	        
	        return new VideoInfo(videoId, videoUrl);
	        
	    } catch (IOException e) {
	        throw new IOException("비디오 업로드 중 오류가 발생했습니다.", e);
	    }
	}

  
}

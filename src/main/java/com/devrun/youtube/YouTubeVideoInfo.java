package com.devrun.youtube;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;

@Component
public class YouTubeVideoInfo {
	private static final String CLIENT_SECRETS = "client_secret.json";
	private static final Collection<String> SCOPES = Arrays.asList("https://www.googleapis.com/auth/youtube.readonly");
	private static final String APPLICATION_NAME = "API code samples";
	private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
	private HttpTransport httpTransport;

	public YouTubeVideoInfo() throws Exception {
		httpTransport = GoogleNetHttpTransport.newTrustedTransport();
	}

	/**
	 * Build and return an authorized API client service.
	 *
	 * @return an authorized API client service
	 * @throws GeneralSecurityException, IOException
	 */
	public YouTube getService(String accessToken) throws GeneralSecurityException, IOException {
		final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
		Credential credential = new GoogleCredential().setAccessToken(accessToken);
		return new YouTube.Builder(httpTransport, JSON_FACTORY, credential).setApplicationName(APPLICATION_NAME)
				.build();
	}

	/**
	 * Call function to create API service object. Define and execute API request.
	 * Print API response.
	 *
	 * @throws GeneralSecurityException, IOException, GoogleJsonResponseException
	 */
	public List<VideoDto> getVideoInfo(List<VideoDto> uploadedVideos, HttpServletResponse httpServletResponse,
			String accessToken) throws GeneralSecurityException, IOException, GoogleJsonResponseException {
		System.out.println("---------------------------------------메타 데이터 받아오기 시작----------------------------------");
		String videoIdlist = "";

		for (VideoDto v : uploadedVideos) {
			videoIdlist += v.getVideoId() + ",";
		}
		System.out.println("videoIdlist : "+ videoIdlist);
		
		Credential credential = new GoogleCredential().setAccessToken(accessToken);

		// YouTube 객체 초기화
		YouTube youtube = new YouTube.Builder(httpTransport, JSON_FACTORY, credential)
				.setApplicationName(APPLICATION_NAME).build();

		// Define and execute the API request
		YouTube.Videos.List request = youtube.videos().list("snippet,contentDetails,statistics");
		VideoListResponse response = request.setId(videoIdlist).execute();

		System.out.println(response);

		for (Video videodata : response.getItems()) {
			for (VideoDto v : uploadedVideos) {
				if (v.getVideoId().equals(videodata.getId())) {
					String videoTotalPlayTime = videodata.getContentDetails().getDuration();
					String period = videoTotalPlayTime;
					Duration duration = Duration.parse(period);
					int totalSeconds = (int) duration.getSeconds();
					v.setTotalPlayTime(totalSeconds);
					System.out.println("VideoId : " + v.getVideoId());
					System.out.println("totalSeconds : " + totalSeconds);
				}
			}
		}
		return uploadedVideos;

	}

}
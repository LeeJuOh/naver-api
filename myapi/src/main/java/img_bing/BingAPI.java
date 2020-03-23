package img_bing;

import java.net.*;
import java.util.*;
import java.awt.image.BufferedImage;
import java.io.*;

import javax.imageio.ImageIO;
import javax.net.ssl.HttpsURLConnection;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class BingAPI {

	static String subscriptionKey = "de9b066f0dfd4b4ca5d04420e13323b0";
	static String host = "https://api.cognitive.microsoft.com";
	static String path = "/bing/v7.0/images/search";
	static String searchTerm = "찜닭";
	static String count = "150"; // 결과 리턴 개수(max 150), == display
	static String offset = "0"; // offset, == start

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		String searchQuery = null;
		String json = null;
		ImageObject imgObj = null;
		ArrayList<String> link_list = new ArrayList<String>();
		ArrayList<String> thum_list = new ArrayList<String>();
		BufferedImage bi = null;
		String savePath = "C://Users/LJO/Desktop/image/bing/";
		String fileFormat = "jpg";
		String saveFileName = null;
		File outputFile = null;
		int loop_cnt = 1;
		boolean flag = true;

		try { // text 인코딩

			searchQuery = URLEncoder.encode(searchTerm, "utf-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("검색어 인코딩 실패", e);
		}

		try { // receive JSON body and parsing

			for (int i = 0; i < loop_cnt; i++) {

				offset = Integer.toString(i * Integer.parseInt(count));
				URL url = new URL(host + path + "?q=" + searchQuery + "&count=" + count + "&offset=" + offset);
				System.out.println(url);

				OkHttpClient client = new OkHttpClient();
				Request request = new Request.Builder().addHeader("Ocp-Apim-Subscription-Key", subscriptionKey).url(url)
						.build();
				Response response = client.newCall(request).execute();
				json = response.body().string();
				// System.out.println(json);

				Gson gson = new Gson();
				imgObj = gson.fromJson(json, ImageObject.class);

				if (flag) { // 반복 횟수 결정
					if (Integer.parseInt(imgObj.totalEstimatedMatches) % Integer.parseInt(count) == 0) {

						loop_cnt = Integer.parseInt(imgObj.totalEstimatedMatches) / Integer.parseInt(count);
					} else {

						loop_cnt = Integer.parseInt(imgObj.totalEstimatedMatches) / Integer.parseInt(count) + 1;
					}
					flag = false;
					System.out.println(imgObj.totalEstimatedMatches + ", loop_cnt : " + loop_cnt);
				}

				System.out.println("파싱 개수 : " + imgObj.value.size());

				for (int j = 0; j < imgObj.value.size(); j++) { // 이미지 url 담기
					System.out.println(j + ": " + imgObj.value.get(j).imageId);
					link_list.add(imgObj.value.get(j).contentUrl);
					thum_list.add(imgObj.value.get(j).thumbnailUrl);
				}
			}

		} catch (Exception e) {

			e.printStackTrace();
		}

		for (int i = 0; i < link_list.size(); i++) { // download 파트

			try {
				saveFileName = searchTerm + "_" + i + "." + fileFormat;
				outputFile = new File(savePath + saveFileName);
				URL img_url = new URL(link_list.get(i));
				System.out.println(img_url);
				bi = ImageIO.read(img_url);
				ImageIO.write(bi, fileFormat, outputFile);
			} catch (Exception e) { // 혹시 이미지 url로 안되면 썸네일 url로 다운로드(되는진 모름 ㅋㅋ)

				System.out.println(e.getMessage());
				URL img_url = new URL(thum_list.get(i));
				System.out.println(img_url);
				bi = ImageIO.read(img_url);
				ImageIO.write(bi, fileFormat, outputFile);
				continue;

			}
		}

	}

}

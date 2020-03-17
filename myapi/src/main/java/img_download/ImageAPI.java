package img_download;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import com.google.gson.Gson;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ImageAPI {
	public static StringBuilder sb;//

	public static void main(String[] args) {

		String client_id = "UWVUkNseKo37aoyl40Uy";
		String client_secret = "uPP2aapn6Y";
		int display = 100;
		String json = null;
		URL img_url = null;
		BufferedImage bi = null;
		String savePath = "C://Users/LJO/Desktop/image/";
		String fileFormat = "jpg";
		String saveFileName = null;
		ImageObject imgObj = null;
		ArrayList<String> link_list = new ArrayList<String>();
		String text = null;
		String query = null;

		try { // text 인코딩
			text = "짜장면";
			query = URLEncoder.encode(text, "utf-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("검색어 인코딩 실패", e);
		}

		try { // api 요청

			for (int start = 1; start <= 1000; start += display) {
				if (start == 901)
					start = 1000;
				String urlstr = "https://openapi.naver.com/v1/search/image?query=" + query + "&display=" + display
						+ "&sort=sim" + "&start=" + start;

				System.out.println(urlstr);
				OkHttpClient client = new OkHttpClient();
				Request request = new Request.Builder().addHeader("X-Naver-Client-Id", client_id)
						.addHeader("X-Naver-Client-Secret", client_secret).url(urlstr).build();

				Response response = client.newCall(request).execute();
				json = response.body().string();
				// System.out.println(json);

				Gson gson = new Gson();
				imgObj = gson.fromJson(json, ImageObject.class);

				for (int i = 0; i < imgObj.items.size(); i++) {
					link_list.add(imgObj.items.get(i).link);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		try { // download

			for (int i = 0; i < link_list.size(); i++) {

				saveFileName = text + "_" + i + "." + fileFormat;
				File outputFile = new File(savePath + saveFileName);
				img_url = new URL(link_list.get(i));
				bi = ImageIO.read(img_url);
				ImageIO.write(bi, fileFormat, outputFile);

			}
		} catch (Exception e) {

			System.out.println("호이");
			e.printStackTrace();
		}
	}
}

package text_download;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import com.google.gson.Gson;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TextAPI {

	public static void main(String[] args) throws IOException {

		String client_id = "UWVUkNseKo37aoyl40Uy";
		String client_secret = "uPP2aapn6Y";
		int display = 100;
		String json = null;
		String savePath = "C://Users/parkwoojin/eclipse-workspace/naver-api/myapi/crawlingdata/";
		TextObject textObj = null;
		ArrayList<String> text_list = new ArrayList<String>();
		String text = null;
		String query = null;
		BufferedOutputStream bs = null;

		try { // text 인코딩
			text = "데이트 음식";
			query = URLEncoder.encode(text, "utf-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("검색어 인코딩 실패", e);
		}

		try { // api 요청

			for (int start = 1; start <= 3000; start += display) {
				if (start == 901)
					start = 1000;
				String urlstr = "https://openapi.naver.com/v1/search/blog.json?query=" + query + "&display=" + display
						+ "&sort=sim" + "&start=" + start;

				System.out.println(urlstr);
				OkHttpClient client = new OkHttpClient();
				Request request = new Request.Builder().addHeader("X-Naver-Client-Id", client_id)
						.addHeader("X-Naver-Client-Secret", client_secret).url(urlstr).build();

				Response response = client.newCall(request).execute();
				json = response.body().string();
				// System.out.println(json);

				Gson gson = new Gson();
				textObj = gson.fromJson(json, TextObject.class);

				for (int i = 0; i < textObj.items.size(); i++) { // 태그 삭제
					
					String match = "[^\uAC00-\uD7A3xfe0-9a-zA-Z\\s]"; 
					//한글유니코드(\uAC00-\uD7A3), 숫자 0~9(0-9), 영어 소문자a~z(a-z), 대문자A~Z(A-Z), 공백(\s)의 단어만 저장		
					String replace_text = textObj.items.get(i).description.replaceAll(match, "");
					replace_text = replace_text.replaceAll("b", "");					
			
					text_list.add(replace_text);
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		try { // write
			
			BufferedWriter fw = new BufferedWriter(new FileWriter(savePath+"/"+text+".csv",true));
			// csv 파일 형태로 저장 			
			for (int i = 0; i < text_list.size(); i++) {

				System.out.println(text_list.get(i));
				
				fw.write(text+","+text_list.get(i));
				fw.newLine();

			}
			fw.flush();
			fw.close();
		} catch (Exception e) {

			e.printStackTrace();
		} finally {

		}

	}
}

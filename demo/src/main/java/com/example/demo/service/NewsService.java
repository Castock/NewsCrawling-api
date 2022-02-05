package com.example.demo.service;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import com.example.demo.dto.NewsDTO;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

// import lombok.extern.slf4j.Slf4j;

// @Slf4j
@Service
public class NewsService {
    public static int UrlPage(String url){
		try{
			Document d = Jsoup.connect(url).get();
			Elements el = d.select(".pgRR");

			Elements PgElement = el.select("a");
			String PgUrl = PgElement.attr("href");
			System.out.println(PgUrl);
			System.out.println();
			
			if (PgUrl.isEmpty()){
				return 1;
			}
			else{
				return PgUrl.charAt(PgUrl.length() - 1);
			}
		}
		catch(IOException e){
			e.printStackTrace();
		}
		return 0;
	}
    public List<NewsDTO> getNews() throws IOException{
		String M_url = "https://finance.naver.com/news/news_list.naver?mode=LSS3D&section_id=101&section_id2=258&section_id3=401";
		int maxPage = UrlPage(M_url);

		for (int page = 1; page <= maxPage; page++){
			final String url = M_url+"&page="+page;
			Connection conn = Jsoup.connect(url);
			try {
				Document document = conn.get();
				return getNewsList(document);				
			}
			catch(IOException e){
				e.getStackTrace(); // 에러의 Exception 내용과 원인을 출력
			}
		}
		return null;
	}

	public List<NewsDTO> getNewsList(Document document) throws IOException{
		Elements artElements = document.getElementsByAttributeValue("class", "articleSubject");
		List<NewsDTO> list = new ArrayList<>();
		for(int i = 0; i <= artElements.size(); i++){
			Element articleElement = artElements.get(i);
			list.add(createNewsDto(articleElement.select("a")));
		}
		return list;				
	}

	private NewsDTO createNewsDto(Elements a) throws IOException {
		NewsDTO newsDto = NewsDTO.builder().build();
		Class<?> clazz = newsDto.getClass();

		Element aElement = a.get(0);
		String articleUrl = aElement.attr("href");
		String title = aElement.attr("title");
		
		FileOutputStream fos = new FileOutputStream(new File("src/main/java/com/example/demo/text/" + title + ".txt"));
		//FileOutputStream fos = new FileOutputStream(new File("C:/Users/ssm11/OneDrive/바탕 화면/News_text/" + title + ".txt")); // 노트북
		OutputStreamWriter osw = new OutputStreamWriter(fos);
		BufferedWriter bw = new BufferedWriter(osw);

		try{
			Document subDoc = Jsoup.connect("https://finance.naver.com" + articleUrl).get();
			
			Elements cElement = subDoc.select("div[id=\"content\"]");
			String content = cElement.text();

			System.out.println(content);
			System.out.println();
			bw.write(content);
			bw.newLine();
		}
		catch(IOException e){ //Jsoup의 connect 부분에서 IOException 오류가 날 수 있으므로 사용
			e.printStackTrace(); // 에러의 발생근원지를 찾아서 단계별로 에러를 출력
		}
		bw.close();
		osw.close();
		fos.close();
		return newsDto;
	}
}

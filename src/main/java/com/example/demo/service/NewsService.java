package com.example.demo.service;
/*
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
*/
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.example.demo.dto.NewsDTO;
import com.example.demo.model.NewsEntity;
import com.example.demo.persistence.NewsRepository;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

// import lombok.extern.slf4j.Slf4j;

// @Slf4j
@Service
public class NewsService {
	@Autowired
	NewsRepository newsRepo;

	private static String News_URL = "https://finance.naver.com/news/news_list.naver?mode=LSS3D&section_id=101&section_id2=258&section_id3=401";
	
    public int UrlPage(String url){
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
		return 1;
	}
	
	public void crawl()
	{
		int maxPage = UrlPage(News_URL);

		for (int page = 1; page <= maxPage; page++){
			final String url = News_URL+"&page="+page;
			Connection conn = Jsoup.connect(url);
			try {
				Document document = conn.get();
				Elements elem = document.select("div[class=\"realtimeNewsList\"");
				
				for(Element e: elem.select("div")){
					if(e.className().equals("articleSubject")){
						NewsEntity newsE = new NewsEntity();

						newsE.setTitle(e.attr("title"));
						newsE.setUrl(e.attr("href"));;

						newsRepo.save(newsE);
					}
				}
			}
			catch(IOException e){
				e.getStackTrace(); // 에러의 Exception 내용과 원인을 출력
			}
		}
	}

    public List<NewsDTO> getNews() throws IOException{
		int maxPage = UrlPage(News_URL);

		for (int page = 1; page <= maxPage; page++){
			final String url = News_URL+"&page="+page;
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
			list.add(createNewsDto(articleElement.select("a"), i));
		}
		return list;				
	}

	private NewsDTO createNewsDto(Elements a, int i) throws IOException {
		NewsDTO newsDto = NewsDTO.builder().build();
		Class<?> clazz = newsDto.getClass();
		Field[] fields = clazz.getDeclaredFields();

		String text;

		Element aElement = a.get(0);
		String articleUrl = aElement.attr("href");
		//String title = aElement.attr("title");
		
		text = articleUrl;
		fields[0].setAccessible(true);
		try{
			fields[0].set(newsDto,text);
		}
		catch (Exception ignored){
		}

		//FileOutputStream fos = new FileOutputStream(new File("/src/main/java/com/example/demo/text/" + title + ".txt"));
		//OutputStreamWriter osw = new OutputStreamWriter(fos);
		//BufferedWriter bw = new BufferedWriter(osw);
		/*

		try{
			Document subDoc = Jsoup.connect("https://finance.naver.com" + articleUrl).get();
			
			Elements cElement = subDoc.select("div[id=\"content\"]");
			String content = cElement.text();

			text = articleUrl;
			fields[1].setAccessible(true);
			try{
				fields[1].set(newsDto,text);
			}
			catch (Exception ignored){
			}

			System.out.println(content);
			System.out.println();
			//bw.write(content);
			//bw.newLine();
		}
		catch(IOException e){ //Jsoup의 connect 부분에서 IOException 오류가 날 수 있으므로 사용
			e.printStackTrace(); // 에러의 발생근원지를 찾아서 단계별로 에러를 출력
		}
		*/
		//bw.close();
		//osw.close();
		//fos.close();
		return newsDto;
	}
}

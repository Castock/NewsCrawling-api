package com.example.demo.controller;

import lombok.RequiredArgsConstructor;
/*
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
*/
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.example.demo.service.NewsService;
import com.example.demo.dto.NewsDTO;
import com.example.demo.model.NewsEntity;
import com.example.demo.persistence.NewsRepository;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
@Component
@RestController
@RequiredArgsConstructor
public class NewsController {

    @Autowired
    private final NewsService newsService;
	
	@Autowired
	NewsRepository newsRepo;

	private static String News_URL = "https://finance.naver.com/news/news_list.naver?mode=LSS3D&section_id=101&section_id2=258&section_id3=401";

	public int UP(){
		return newsService.UrlPage(News_URL);
	}
	
	@GetMapping("dd")
	public String crawl()
	{
		int maxPage = UP();

		for (int page = 1; page <= maxPage; page++){
			final String url = News_URL+"&page="+page;
			Connection conn = Jsoup.connect(url);
			try {
				Document document = conn.get();
				Elements artElements = document.getElementsByAttributeValue("class", "articleSubject");

				for(int i = 0; i <= artElements.size(); i++){
					try{
						Element articleElement = artElements.get(i);
						Elements aElements = articleElement.select("a");
						Element aElement = aElements.get(0);

						String articleTitle = aElement.attr("title");
						String articleUrl = aElement.attr("href");
						
						NewsEntity newsE = new NewsEntity();

						newsE.setTitle(articleTitle);
						newsE.setUrl(articleUrl);
						newsRepo.save(newsE);
						
						System.out.println(articleTitle);
						System.out.println("https://finance.naver.com" + articleUrl);
					}
					catch (Exception e){
						e.getStackTrace();
					}
				}
			}
			catch(IOException e){
				e.getStackTrace(); // 에러의 Exception 내용과 원인을 출력
				return e.toString();
			}
		}
		return "a";
	}

	@GetMapping("/test")
	public String testControllerWithPath() {
        return "Hello World! testGetMapping ";
    }
    @GetMapping("/text")
    public List<NewsDTO> getNews(HttpServletRequest request) throws IOException {
        return newsService.getNews();
    }

    @GetMapping("/crawling")
	public List<String> getNewsList(HttpServletRequest request) throws IOException {
		List<String> list = new ArrayList<>();

		final String M_url = "https://finance.naver.com/news/news_list.naver?mode=LSS3D&section_id=101&section_id2=258&section_id3=401";
        Connection d = Jsoup.connect(M_url);
		Document docu = d.get();
        Elements el = docu.select(".pgRR");

        Elements PgElement = el.select("a");
        String PgUrl = PgElement.attr("href");

        System.out.println(PgUrl);
        System.out.println();

        int maxPage;
        
        if (PgUrl.isEmpty()){
            maxPage = 1;
        }
        else{
            maxPage = PgUrl.charAt(PgUrl.length() - 1);
        }

		for (int page = 1; page <= maxPage; page++){
			try {
				final String url = M_url+"&page="+page;
				Connection conn = Jsoup.connect(url);
				Document document = conn.get();	

				Elements artElements = document.getElementsByAttributeValue("class", "articleSubject");

				for(int i = 0; i <= artElements.size(); i++){
					try{
						Element articleElement = artElements.get(i);
						Elements aElements = articleElement.select("a");
						Element aElement = aElements.get(0);

						String articleUrl = aElement.attr("href");
						String title = aElement.attr("title");
						
						//FileOutputStream fos = new FileOutputStream(new File("/src/main/java/com/example/demo/text/" + title + ".txt"));
						//OutputStreamWriter osw = new OutputStreamWriter(fos);
						//BufferedWriter bw = new BufferedWriter(osw);

						try{
							Document subDoc = Jsoup.connect("https://finance.naver.com" + articleUrl).get();
							Elements cElement = subDoc.select("#content");
							String content = cElement.text();

							list.add(title);

							System.out.println(title);
							System.out.println();
							//bw.write(content);
							//bw.newLine();
						}
						catch(IOException e){ //Jsoup의 connect 부분에서 IOException 오류가 날 수 있으므로 사용
							e.printStackTrace(); // 에러의 발생근원지를 찾아서 단계별로 에러를 출력
						}
						//bw.close();
						//osw.close();
						//fos.close();
					}
					catch (Exception e){
						e.getStackTrace();
					}
				}
			}
			catch(Exception e){
				e.getStackTrace(); // 에러의 Exception 내용과 원인을 출력
			}
		}
		System.out.println("끝");
		return list;
	}
}

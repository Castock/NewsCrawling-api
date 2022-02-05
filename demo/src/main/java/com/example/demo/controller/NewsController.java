package com.example.demo.controller;

import lombok.RequiredArgsConstructor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import com.example.demo.service.NewsService;


import java.util.List;
import com.example.demo.dto.NewsDTO;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class NewsController {

    @Autowired
    private final NewsService newsService;

    @GetMapping("/text")
    public List<NewsDTO> getNews() throws IOException {
        return newsService.getNews();
    }

    @GetMapping("/crawling")
	public void getNewsList() throws IOException {
		String M_url = "https://finance.naver.com/news/news_list.naver?mode=LSS3D&section_id=101&section_id2=258&section_id3=401";
        Document d = Jsoup.connect(M_url).get();
        Elements el = d.select(".pgRR");

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
		//int maxPage = UrlPage(M_url);

		for (int page = 1; page <= maxPage; page++){
			try {
				String url = M_url+"&page="+page;
				Document doc = Jsoup.connect(url).get();
				Elements elements = doc.getElementsByAttributeValue("class", "realtimeNewsList");

				Element element = elements.get(0);
				Elements artElements = element.getElementsByAttributeValue("class", "articleSubject");

				for(int i = 0; i <= artElements.size(); i++){
					try{
						Element articleElement = artElements.get(i);
						Elements aElements = articleElement.select("a");
						Element aElement = aElements.get(0);

						String articleUrl = aElement.attr("href");
						String title = aElement.attr("title");
						
						//FileOutputStream fos = new FileOutputStream(new File("D:/Desktop/File_test/" + title + ".txt"));
						FileOutputStream fos = new FileOutputStream(new File("C:/Users/ssm11/OneDrive/바탕 화면/News_text/" + title + ".txt")); // 노트북
						OutputStreamWriter osw = new OutputStreamWriter(fos);
						BufferedWriter bw = new BufferedWriter(osw);

						try{
							Document subDoc = Jsoup.connect("https://finance.naver.com" + articleUrl).get();
							//Element contentElement = subDoc.getElementById("content");
							//Elements contentElement = subDoc.select("div#content.articleCont");

							//Elements contentElement = subDoc.select("#contentarea contentarea_left");
							Elements conElement = subDoc.getElementsByAttributeValue("class", "boardView size4");

							Elements cElement = conElement.select("#content");
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
	}
}

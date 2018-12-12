package soa.web;

import org.apache.camel.ProducerTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Controller
public class SearchController {

  private final ProducerTemplate producerTemplate;

  @Autowired
  public SearchController(ProducerTemplate producerTemplate) {
    this.producerTemplate = producerTemplate;
  }

  @RequestMapping("/")
  public String index() {
    return "index";
  }


  @RequestMapping(value = "/search")
  @ResponseBody
  public Object search(@RequestParam("q") String q) {
      Pattern pattern = Pattern.compile("max:(.*)");
      Matcher matcher = pattern.matcher(q);
      matcher.find();
      Map<String, Object> headers = new HashMap<>();
      try{
          int max = Integer.parseInt(matcher.group(1));
          headers.put("CamelTwitterCount", max);
          String newQuery = q.replace("max:"+max, "");
          headers.put("CamelTwitterKeywords", newQuery);
      }
      catch(IllegalStateException e){
          System.out.println("User doesn't want limit");
          return producerTemplate.requestBodyAndHeader("direct:search", "", "CamelTwitterKeywords", q);
      }

    return producerTemplate.requestBodyAndHeaders("direct:search", "", headers);
  }
}
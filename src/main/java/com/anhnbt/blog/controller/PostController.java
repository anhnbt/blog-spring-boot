package com.anhnbt.blog.controller;

import com.anhnbt.blog.entities.Post;
import com.anhnbt.blog.model.AuthorLdJson;
import com.anhnbt.blog.model.MetaTag;
import com.anhnbt.blog.model.SchemaLdJson;
import com.anhnbt.blog.service.PostService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping
public class PostController {

    private Logger logger = LoggerFactory.getLogger(PostController.class);

    @Autowired
    private PostService postService;

    @Value(value = "${app.base-url}")
    private String baseUrl;

    @GetMapping("/p/{slug}.html")
    private ModelAndView details(@PathVariable("slug") String slug) {
        ModelAndView modelAndView = new ModelAndView("posts");
        Post post = postService.findByPostName(slug).orElseThrow(() -> new RuntimeException("Not found"));
        MetaTag metaTag = new MetaTag();
        metaTag.setCanonical(baseUrl + "/p/" + slug + ".html");
        metaTag.setUrl(baseUrl + "/p/" + slug + ".html");
        metaTag.setTitle(post.getPostTitle());
        metaTag.setType("article");
//        metaTag.setDescription(post.getPostContent());
        metaTag.setImage(baseUrl + "/uploads/" + post.getPostThumb());

        SchemaLdJson json = new SchemaLdJson();
        json.setContext("https://schema.org");
        json.setType("NewsArticle");
        json.setHeadline(post.getPostTitle());
        json.setDatePublished(post.getPostDate());
        json.setDateModified(post.getPostModified());
        json.setAuthor(new AuthorLdJson("Person", "Nguy???n B?? Tu???n Anh", baseUrl));
        json.setImage(List.of(baseUrl + "/uploads/" + post.getPostThumb()));
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonAsString = null;
        try {
            jsonAsString = objectMapper.writeValueAsString(json);

            post.setPostViewCount(post.getPostViewCount() + 1);
            if (postService.save(post) == null) {
                logger.info("Kh??ng c???p nh???t ???????c post view");
            }
        } catch (JsonProcessingException e) {
            // TODO 500 error page
        } catch (Exception e) {
            // TODO 500 error page
        }

        modelAndView.addObject("schemaLdJson", jsonAsString);
        modelAndView.addObject("metaTag", metaTag);
        modelAndView.addObject("post", post);
        return modelAndView;
    }
}

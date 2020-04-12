package app.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.ResourceResolver;
import org.springframework.web.servlet.resource.ResourceResolverChain;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        // adding content negotiation for *.map files which are application/json
        configurer.replaceMediaTypes(Map.of("map", MediaType.APPLICATION_JSON));
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        /*
        adding resource handlers for single page app routing
        - for example, let's say you have an angular app with the
          following page route /start/home, if you go to this page,
          spring will not know how to handle it, so we have to tell
          it to load index.html so angular can take care of routing
          it to the correct page
         */
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/")
                .setCachePeriod(31556926)
                .resourceChain(true)
                .addResolver(new WebResourceResolver());
    }

    /*
    Custom web resolver
    - ignores api, actuator calls
    - if it doesn't have a file extension, returns index.html
     */
    private class WebResourceResolver implements ResourceResolver {
        private final Resource index = new ClassPathResource("/static/index.html");
        @Override
        public Resource resolveResource(HttpServletRequest request, String requestPath, List<? extends Resource> locations, ResourceResolverChain chain) {
            if (requestPath.matches("^(api|actuator).+")){
                return null;
            }
            if (requestPath.equals("index.html")) {
                return index;
            }
            if (requestPath.matches(".+(\\.(js|json|css|html|txt|ico|svg|eot|ttf|woff|map))$")) {
                Resource resource = new ClassPathResource(resolveUrlPath(requestPath, locations, chain));
                return resource.exists() ? resource : null;
            }
            return index;
        }

        @Override
        public String resolveUrlPath(String resourcePath, List<? extends Resource> locations, ResourceResolverChain chain) {
            return "/static/" + resourcePath;
        }
    }
}

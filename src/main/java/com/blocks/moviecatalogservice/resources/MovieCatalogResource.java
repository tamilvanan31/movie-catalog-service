package com.blocks.moviecatalogservice.resources;

import com.blocks.moviecatalogservice.models.CatalogItem;
import com.blocks.moviecatalogservice.models.Movie;
import com.blocks.moviecatalogservice.models.Rating;
import com.blocks.moviecatalogservice.models.UserRating;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/catalog")
public class MovieCatalogResource {

    @Autowired
    private RestTemplate restTemplate;
    // RestTemplate is thread safe, so one call will not affect the other call

    @RequestMapping("/{userId}")
    public List<CatalogItem> getCatalog(@PathVariable("userId") String userId) {

        // Get all rated movie id, for each movie id, call movie info service and get details and put them all together

        UserRating userRatings = restTemplate.getForObject("http://localhost:8083/rating/users/" + userId, UserRating.class);

        return userRatings.getUserRating().stream().map(rating -> {
             Movie movie = restTemplate.getForObject("http://localhost:8082/movies/" + rating.getMovieId(), Movie.class);
            return new CatalogItem(movie.getName(), "Description", rating.getRating());
        }).collect(Collectors.toList());
    }
}

            /*  WebClient
                Movie movie = webClientBuilder.build()
                    .get()
                    .uri("http://localhost:8082/movies/" + rating.getMovieId())
                    .retrieve()
                    .bodyToMono(Movie.class)
                    .block();
             */
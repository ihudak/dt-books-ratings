package com.dynatrace.ratings.controller;

import com.dynatrace.ratings.exception.BadRequestException;
import com.dynatrace.ratings.exception.ResourceNotFoundException;
import com.dynatrace.ratings.model.Rating;
import com.dynatrace.ratings.repository.RatingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/")
public class RatingController {
    @Autowired
    private RatingRepository ratingRepository;

    // get all ratings
    @GetMapping("/ratings")
    public List<Rating> getAllRatings() {
        return ratingRepository.findAll(Sort.by(Sort.Direction.ASC, "isbn", "email", "createdAt"));
    }

    // get ratings by a client
    @GetMapping("/ratings/findByEmail")
    public List<Rating> getRatingsByEmail(@RequestParam String email) {
        return ratingRepository.findByEmail(email);
    }

    // get ratings for a book
    @GetMapping("/ratings/findByISBN")
    public List<Rating> getRatingsByISBN(@RequestParam String isbn) {
        return ratingRepository.findByEmail(isbn);
    }

    // get a rating by id
    @GetMapping("/ratings/{id}")
    public Rating getRatingById(@PathVariable Long id) {
        Optional<Rating> rating = ratingRepository.findById(id);
        if (rating.isEmpty()) {
            throw new ResourceNotFoundException("Rating not found");
        }
        return rating.get();
    }

    // create a rating
    @PostMapping("/ratings")
    public Rating createRating(@RequestBody Rating rating) {
        return ratingRepository.save(rating);
    }

    // update a rating
    @PutMapping("/ratings/{id}")
    public Rating updateRatingById(@PathVariable Long id, @RequestBody Rating rating) {
        Optional<Rating> ratingDb = ratingRepository.findById(id);
        if (ratingDb.isEmpty()) {
            throw new ResourceNotFoundException("Rating not found");
        } else if (rating.getId() != id || ratingDb.get().getId() != id) {
            throw new BadRequestException("bad rating id");
        }
        return ratingRepository.save(rating);
    }

    // delete a rating by id
    @DeleteMapping("/ratings/{id}")
    public void deleteRatingById(@PathVariable Long id) {
        ratingRepository.deleteById(id);
    }

    // delete all ratings
    @DeleteMapping("/ratings/delete-all")
    public void deleteAllRatings() {
        ratingRepository.deleteAll();
    }
}

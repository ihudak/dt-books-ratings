package com.dynatrace.ratings.controller;

import com.dynatrace.ratings.exception.BadRequestException;
import com.dynatrace.ratings.exception.ResourceNotFoundException;
import com.dynatrace.ratings.model.Book;
import com.dynatrace.ratings.model.Client;
import com.dynatrace.ratings.model.Rating;
import com.dynatrace.ratings.repository.BookRepository;
import com.dynatrace.ratings.repository.ClientRepository;
import com.dynatrace.ratings.repository.ConfigRepository;
import com.dynatrace.ratings.repository.RatingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/ratings")
public class RatingController extends HardworkingController {
    @Autowired
    private RatingRepository ratingRepository;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private ConfigRepository configRepository;
    private Logger logger = LoggerFactory.getLogger(RatingController.class);

    // get all ratings
    @GetMapping("")
    public List<Rating> getAllRatings() {
        return ratingRepository.findAll(Sort.by(Sort.Direction.ASC, "isbn", "email", "createdAt"));
    }

    // get ratings by a client
    @GetMapping("/findByEmail")
    public List<Rating> getRatingsByEmail(@RequestParam String email) {
        return ratingRepository.findByEmail(email);
    }

    // get ratings for a book
    @GetMapping("/findByISBN")
    public List<Rating> getRatingsByISBN(@RequestParam String isbn) {
        return ratingRepository.findByEmail(isbn);
    }

    // get a rating by id
    @GetMapping("/{id}")
    public Rating getRatingById(@PathVariable Long id) {
        Optional<Rating> rating = ratingRepository.findById(id);
        if (rating.isEmpty()) {
            ResourceNotFoundException ex = new ResourceNotFoundException("Rating not found");
            logger.error(ex.getMessage());
            throw ex;
        }
        return rating.get();
    }

    // create a rating
    @PostMapping("")
    public Rating createRating(@RequestBody Rating rating) {
        simulateHardWork();
        simulateCrash();
        this.verifyBook(rating.getIsbn());
        this.verifyClient(rating.getEmail());
        logger.debug("Creating Rating for book " + rating.getIsbn() + " from user " + rating.getEmail());
        return ratingRepository.save(rating);
    }

    // update a rating
    @PutMapping("/{id}")
    public Rating updateRatingById(@PathVariable Long id, @RequestBody Rating rating) {
        Optional<Rating> ratingDb = ratingRepository.findById(id);
        if (ratingDb.isEmpty()) {
            ResourceNotFoundException ex = new ResourceNotFoundException("Rating not found");
            logger.error(ex.getMessage());
            throw ex;
        } else if (rating.getId() != id || ratingDb.get().getId() != id) {
            BadRequestException ex = new BadRequestException("bad rating id");
            logger.error(ex.getMessage());
            throw ex;
        }
        return ratingRepository.save(rating);
    }

    // delete a rating by id
    @DeleteMapping("/{id}")
    public void deleteRatingById(@PathVariable Long id) {
        ratingRepository.deleteById(id);
    }

    // delete all ratings
    @DeleteMapping("/delete-all")
    public void deleteAllRatings() {
        ratingRepository.truncateTable();
    }

    @Override
    public ConfigRepository getConfigRepository() {
        return configRepository;
    }


    private void verifyClient(String email) {
        logger.info("Verifying client " + email);
        Client client = clientRepository.getClientByEmail(email);
        if (null == client) {
            ResourceNotFoundException ex = new ResourceNotFoundException("Client is not found by email " + email);
            logger.error(ex.getMessage());
            throw ex;
        }
        Client[] clients = clientRepository.getAllClients();
        logger.debug(clients.toString());
    }

    private Book verifyBook(String isbn) {
        logger.info("Verifying book " + isbn);
        Book book = bookRepository.getBookByISBN(isbn);
        if (null == book) {
            ResourceNotFoundException ex = new ResourceNotFoundException("Book not found by isbn " + isbn);
            logger.error(ex.getMessage());
            throw ex;
        }
        Book[] books = bookRepository.getAllBooks();
        logger.debug(books.toString());
        return book;
    }
}

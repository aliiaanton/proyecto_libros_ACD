package com.bookmatch.backend.controller;

import com.bookmatch.backend.dto.ReviewRequest;
import com.bookmatch.backend.entity.Review;
import com.bookmatch.backend.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    // Publicar una reseña
    @PostMapping
    public ResponseEntity<?> createReview(@RequestBody ReviewRequest request) {
        try {
            Review savedReview = reviewService.saveReview(request);
            return ResponseEntity.ok("Reseña guardada. ID: " + savedReview.getReviewId());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Obtiene todas las reseñas de un libro específico.
     *
     * @param googleBookId El ID del libro en Google Books.
     * @return Lista de reseñas del libro.
     */
    @GetMapping("/{googleBookId}")
    public ResponseEntity<List<Review>> getBookReviews(@PathVariable String googleBookId) {
        return ResponseEntity.ok(reviewService.getReviewsByBook(googleBookId));
    }
}
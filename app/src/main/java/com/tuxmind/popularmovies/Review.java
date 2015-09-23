package com.tuxmind.popularmovies;

/**
 * Created by maddom73 on 08/09/15.
 */
public class Review {
    public final String reviewAuthor;
    public final String reviewContent;

    public Review(String reviewAuthor, String reviewContent) {
        this.reviewAuthor = reviewAuthor;
        this.reviewContent = reviewContent;

        System.out.println("reviewAuthor: " + reviewAuthor);
        System.out.println("reviewContent: " + reviewContent);
    }
}

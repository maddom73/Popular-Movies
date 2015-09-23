package com.tuxmind.popularmovies;

/**
 * Created by maddom73 on 06/09/15.
 */
public final class Trailer {

    public final String trailerName;
    public final String trailerId;


    public Trailer(String trailerName, String trailerId) {
        this.trailerName = trailerName;
        this.trailerId = trailerId;
        System.out.println("trailerName: " + trailerName);
    }
}

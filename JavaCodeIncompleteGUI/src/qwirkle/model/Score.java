package qwirkle.model;

public class Score {
    private final long startTime = System.currentTimeMillis();
    private int moves;
    private int duration;
    private int points;

    public Score() {
        this.moves = 0;
        this.duration = 0;
        this.points = 0;
    }

    public Score(int moves, int duration, int points) {
        this.moves = moves;
        this.duration = duration;
        this.points = points;
    }

    private void addMove(){
        moves++;
    }

    /**
     * Converting from nanoseconds to seconds
     * @System.currentTimeMillis is great because it isn't affected
     * by local date and time setting changes
     */
    private void setDuration() {
        long endTime = System.currentTimeMillis();
        duration +=  ((endTime - startTime) / 1000);
    }

    public int getDuration() {
        setDuration();
        return duration;
    }

}

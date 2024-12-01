public class Difficult {

    public int n_mines;
    public int rows;
    public int cols;

    Difficult(int n_mines, int rows, int cols) {
        this.n_mines = n_mines;
        this.rows = rows;
        this.cols = cols;
    }

    public enum difficulty{
        easy,
        medium,
        hard
    }

}

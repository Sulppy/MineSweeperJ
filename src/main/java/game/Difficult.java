package game;

import java.io.Serializable;

public class Difficult implements Serializable {

    public final int n_mines;
    public final int rows;
    public final int cols;

    Difficult(int n_mines, int rows, int cols) {
        this.n_mines = n_mines;
        this.rows = rows;
        this.cols = cols;
    }

    public enum difficulty{
        easy("Лёгкий"),
        medium("Средний"),
        hard("Сложный");

        private final String name;

        difficulty(String str) {
            this.name = str;
        }

        public String getName() {
            return name;
        }
    }

}

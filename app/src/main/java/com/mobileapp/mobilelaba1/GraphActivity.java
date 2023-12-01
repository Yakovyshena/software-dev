package com.mobileapp.mobilelaba1;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.softmoore.android.graphlib.Function;
import com.softmoore.android.graphlib.Graph;
import com.softmoore.android.graphlib.GraphView;
import com.softmoore.android.graphlib.Label;
import com.softmoore.android.graphlib.Point;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class GraphActivity extends AppCompatActivity {

    public static final String FILE_NAME = "data.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph); // Встановіть вміст нашого XML-файлу
        List<Point> points = getPoints();

        Graph graph = new Graph.Builder()
                .setWorldCoordinates(-2, 7, -10, 10)
                .addLineGraph(points, Color.BLUE)
                .setXTicks(new double[] {-2, -1, 1, 2, 3, 4, 5, 6, 7})
                .setYTicks(new double[] { -9, -7, -5, -3, 0, 3, 5, 8, 10})
                .build();

        GraphView graphView = findViewById(R.id.graph_view);
        graphView.setGraph(graph);
        setTitle("Empty Graph");
        TextView textView = findViewById(R.id.graph_view_label);
        textView.setText("Графік, побудований на основі табуляції функції");
        String s = load();
    }

    public String load() {
        FileInputStream fis = null;

        try {
            fis = openFileInput(FILE_NAME);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String text;

            while ((text = br.readLine()) != null) {
                sb.append(text).append("\n");
            }

            return sb.toString();

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public List<Point> getPoints() {
        String input = load();

        String[] lines = input.split("\n");
        List<Point> points = new ArrayList<>();

        for (int i = 2; i < lines.length; i++) {
            String[] parts = lines[i].split("\t");
            if (parts.length == 2) {
                double first = Double.parseDouble(parts[0]);
                double second = Double.parseDouble(parts[1]);
                Point p = new Point(first, second);
                points.add(p);
            }
        }
        return points;
    }
    public void openMainPage(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}


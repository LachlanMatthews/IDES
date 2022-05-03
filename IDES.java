package ss.assignment3;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.stream.IntStream;

public class IDES
{
    static class Event
    {
        String name;
        double avg = 0;
        double stdev = 0;
        double weight;

        Event(String n, double w)
        {
            name = n;
            weight = w;
        }
    }

    static void input(ArrayList<Event> d) throws FileNotFoundException
    {
        File file = new File("Events.txt");
        Scanner scanner = new Scanner(file);
        int lineCount = 0;
        int eventCount = 0;
        while (scanner.hasNextLine())
        {
            String line = scanner.nextLine();
            if (lineCount != 0)
            {
                String[] split = line.split(":");
                for (int i = 0; i < split.length; i+= 2)
                {
                    d.add(new Event(split[i], Double.parseDouble(split[i + 1])));
                }
            }
            else
            {
                eventCount = Integer.parseInt(line);
            }
            lineCount++;
        }

        for (int i = 0; i < eventCount; i++)
        {
            file = new File("Base-Data.txt");
            scanner = new Scanner(file);
            ArrayList<Double> baseData = new ArrayList<>();
            double sum = 0;
            double measureCount = 0;
            while (scanner.hasNextLine())
            {
                String line = scanner.nextLine();
                String[] split = line.split(":");
                baseData.add(Double.parseDouble(split[i]));
                sum += Double.parseDouble(split[i]);
                measureCount++;
            }
            double avg = sum / measureCount;
            d.get(i).avg = avg;

            double sum2 = 0;
            for (Double x : baseData)
            {
                x -= avg;
                x *= x;
                sum2 += x;
            }
            d.get(i).stdev = Math.sqrt(sum2 / measureCount);
        }
    }

    static void report(ArrayList<Event> d)
    {
        System.out.printf("%-30s %10s %10s %10s", "Event", "Average", "Stdev", "Weight");
        for (Event e : d)
        {
            System.out.printf("\n%-30s %10.2f %10.2f %10s", e.name, e.avg, e.stdev, e.weight);
        }
    }

    static double calcThreshold(ArrayList<Event> d)
    {
        double sum = 0;
        for (Event e : d)
        {
            sum += e.weight;
        }
        double threshold = 2 * sum;
        System.out.printf("\n\n%-15s %5.2f\n\n", "Threshold", threshold);
        return threshold;
    }

    static void test(ArrayList<Event> d, double threshold) throws FileNotFoundException
    {
        File file = new File("Test-Events.txt");
        Scanner scanner = new Scanner(file);
        ArrayList<Double> testEvents = new ArrayList<>();
        int lineCount = 0;
        while (scanner.hasNextLine())
        {
            lineCount++;
            String[] split = scanner.nextLine().split(":");
            for (String s : split)
            {
                testEvents.add(Double.parseDouble(s));
            }
        }
        int eventTypeCount = d.size();
        for (int i = 0; i < lineCount * eventTypeCount; i+= eventTypeCount)
        {
            for (int j = 0; j < eventTypeCount; j++)
            {
                double x = (testEvents.get(i + j) - d.get(j).avg) / d.get(j).stdev;
                if (x < 0) //Turn into positive number if negative
                {
                    x *= -1;
                }
                testEvents.set(i + j, x * d.get(j).weight);
            }
        }

        scanner = new Scanner(file);
        int line = 1;
        for (int i = 0; i < lineCount * eventTypeCount; i+= eventTypeCount)
        {
            String alarm = "";
            double sum = 0;
            for (int j = 0; j < eventTypeCount; j++)
            {
                sum += testEvents.get(i + j);
            }

            if (sum <= threshold)
            {
                alarm = "No";
            }
            if (sum > threshold)
            {
                alarm = "Yes";
            }

            System.out.println("Line " + line + " -- " + scanner.nextLine() + " Distance: " + sum + " Alarm: " + alarm);
            line++;
        }
    }

    public static void main(String[] args) throws FileNotFoundException
    {
        ArrayList<Event> data = new ArrayList<>();

        input(data);
        report(data);
        double threshold = calcThreshold(data);
        test(data, threshold);
    }
}

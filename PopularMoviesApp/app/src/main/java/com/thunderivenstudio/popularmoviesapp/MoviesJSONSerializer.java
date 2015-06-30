package com.thunderivenstudio.popularmoviesapp;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;

/**
 * Created by Vincent Ngo on 6/27/2015.
 * This class is used to serialize the Movies class into JSON
 */
public class MoviesJSONSerializer {
    private Context mContext;
    private String mFilename;

    public MoviesJSONSerializer(Context c,String filename) {
        mContext=c;
        mFilename=filename;
    }

    public void saveMovies(ArrayList<Movies> movies) throws JSONException,IOException{
        JSONArray array=new JSONArray();
        for (Movies m:movies) {
            array.put(m.toJSON());
        }

        // Write the file to disk
        Writer writer=null;
        try {
            OutputStream out=mContext.openFileOutput(mFilename,Context.MODE_PRIVATE);
            writer=new OutputStreamWriter(out);
            writer.write(array.toString());
        } finally {
            if (writer!=null) {
                writer.close();
            }
        }
    }

    public ArrayList<Movies> loadMovies() throws IOException,JSONException {
        ArrayList<Movies> movies=new ArrayList<Movies>();
        BufferedReader reader=null;
        try {
            // Open and read the file into a StringBuilder
            InputStream in=mContext.openFileInput(mFilename);
            reader=new BufferedReader(new InputStreamReader(in));
            StringBuilder jsonString=new StringBuilder();
            String line=null;
            while ((line=reader.readLine())!=null) {
                jsonString.append(line);

            }

            JSONArray array=(JSONArray) new JSONTokener(jsonString.toString()).nextValue();
            for (int i=0;i<array.length();i++) {
                movies.add(new Movies(array.getJSONObject(i)));
            }
        } catch (FileNotFoundException e) {
            // Ignore this one
        } finally {
            if (reader!=null) {
                reader.close();
            }
        }
        return movies;
    }
}

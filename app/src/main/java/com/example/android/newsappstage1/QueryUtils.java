package com.example.android.newsappstage1;

import android.text.Html;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public final class QueryUtils {
    public static final String LOG_TAG = QueryUtils.class.getName();

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    /**
     * Return a list of {@link News} objects that has been built up from
     * parsing the given JSON response.
     */
    private static List<News> extractNewsFromJson(String newsJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(newsJSON)) {
            return null;
        }
        // Create an empty ArrayList of news
        List<News> news = new ArrayList<>();
        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {
            JSONObject baseJsonResponse = new JSONObject(newsJSON);
            JSONObject newsObject = baseJsonResponse.getJSONObject("response");
            JSONArray newsArray = newsObject.getJSONArray("results");
            // For each news in the newsArray, create an {@link News} object
            for (int i = 0; i < newsArray.length(); i++) {
                JSONObject currentNews = newsArray.getJSONObject(i);
                String sectionName = currentNews.optString("sectionName");
                String webPublicationDate = currentNews.optString("webPublicationDate");
                String webTitle = currentNews.optString("webTitle");
                String webUrl = currentNews.optString("webUrl");
                String thumbnailUrl = null;
                String bodyText = null;
                if (currentNews.has("fields")) {
                    JSONObject fieldsObject = currentNews.getJSONObject("fields");
                    thumbnailUrl = fieldsObject.optString("thumbnail");
                    // Extract the value for the key called "body" and converting from html to plain text.
                    bodyText = Html.fromHtml(fieldsObject.optString("body")).toString();
                    // Removing all blank lines in body text.
                    bodyText = bodyText.replaceAll("(?m)^[ \t]*\r?\n", "");
                }
                String authorName = null;
                if (currentNews.has("tags")) {
                    JSONArray tagsArray = currentNews.getJSONArray("tags");
                    if (tagsArray.length() > 0) {
                        JSONObject tagsObject = tagsArray.getJSONObject(0);
                        if (tagsObject.has("webTitle")) {
                            authorName = tagsObject.getString("webTitle");
                        }
                    }
                }
                // Create a new {@link news} object from the JSON response.
                News newsHeadline = new News(thumbnailUrl, sectionName, webTitle, bodyText, authorName, webPublicationDate, webUrl);
                // Add the new {@link News} to the list of news.
                news.add(newsHeadline);
            }
        } catch (JSONException e) {
            Log.e("QueryUtils", "Problem parsing the news JSON results", e);
        }
        // Return the list of news
        return news;
    }

    /**
     * Query the Guardian dataset and return a list of {@link News} objects.
     */
    public static List<News> fetchNewsData(String requestUrl) {
        Log.i(LOG_TAG, "TEST: fetchNewsData() called");
        // Create URL object
        URL url = createUrl(requestUrl);
        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }
        // Extract relevant fields from the JSON response and create a list of {@link news}
        List<News> news = extractNewsFromJson(jsonResponse);
        // Return the list of {@link news}
        return news;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";
        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the news JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }
}
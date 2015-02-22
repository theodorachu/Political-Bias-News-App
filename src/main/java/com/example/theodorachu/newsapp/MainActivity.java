package com.example.theodorachu.newsapp;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.InputSource;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import android.widget.TextView;
import android.os.AsyncTask;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import android.util.Log;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends ActionBarActivity {

    private TextView rss;
    private ImageView img;

    private class DownloadImageTask extends AsyncTask <String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

    private class RSSHandler extends DefaultHandler {
        private ArrayList<String> titles;
        private boolean beginArticles, title, firstTitle;
        private String fullTitle = "";

        public RSSHandler(ArrayList<String> titles) {
            this.titles = titles;
        }

        public void characters(char[] ch, int start, int length) throws SAXException {
            String cdata = new String(ch, start, length);
            if (beginArticles && title && firstTitle) {
                fullTitle += cdata.trim();
            }
        }

        public void startElement(String uri, String localName, String qName,
                                 Attributes attrs) throws SAXException {
            if (localName.equals("item")) {
                beginArticles = true;
            }
            if (beginArticles) {
                if (!firstTitle && localName.equals("title")) {
                    firstTitle = true;
                    title = true;
                } else {
                    title = false;
                }
                if (localName.equals("thumbnail")) {
                    String thumburl = attrs.getValue("url");
                    String urls[] = new String[1];
                    urls[0] = thumburl;
                    new DownloadImageTask(img)
                            .execute(urls);
                }
            }
        }

        public void endElement(String uri, String localName, String qName)
                throws SAXException {
            if(localName.equals("item")) {
                firstTitle = false;
                titles.add(fullTitle);
                fullTitle = "";
            }
        }

    }

    private class DownloadFilesTask extends AsyncTask<URL, Void, ArrayList<String>> {

        @Override
        protected ArrayList<String> doInBackground(URL... urls) {
            try {
                ArrayList<String> titles = new ArrayList<String>();
                SAXParserFactory factory = SAXParserFactory.newInstance();
                SAXParser saxParser = factory.newSAXParser();
                XMLReader xmlReader = saxParser.getXMLReader();

                RSSHandler rssHandler = new RSSHandler(/*rssResult, */titles);
                xmlReader.setContentHandler(rssHandler);
                InputSource inputSource = new InputSource(urls[0].openStream());
                xmlReader.parse(inputSource);

                //hacky lol
                final ArrayList<String> temp = titles;

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        for(int i = 0; i < temp.size(); ++i)
                            rss.append("\n" + temp.get(i));
                    }
                });

            } catch (IOException | ParserConfigurationException | SAXException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rss = (TextView) findViewById(R.id.rss);
        img = (ImageView) findViewById(R.id.img);

        try {
            URL rssUrl = new URL("http://time.com/politics/feed/");
            URL[] urls = new URL[1];
            urls[0] = rssUrl;

            DownloadFilesTask task = new DownloadFilesTask();
            task.execute(urls);
        } catch (IOException e) {
            e.printStackTrace();
            rss.setText(e.getMessage());
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

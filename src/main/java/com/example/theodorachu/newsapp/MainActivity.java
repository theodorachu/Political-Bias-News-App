package com.example.theodorachu.newsapp;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.os.AsyncTask;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.GridLayout.LayoutParams;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;
import android.util.Log;
import android.content.Intent;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import java.io.InputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends ActionBarActivity {

    private LinearLayout mainView;
//    private TextView rss;
//    private ImageView img;

//    private class DownloadImageTask extends AsyncTask <String, Void, Bitmap> {
//        ImageView bmImage;
//
//        public DownloadImageTask(ImageView bmImage) {
//            this.bmImage = bmImage;
//        }
//
//        protected Bitmap doInBackground(String... urls) {
//            String urldisplay = urls[0];
//            Bitmap mIcon11 = null;
//            try {
//                InputStream in = new java.net.URL(urldisplay).openStream();
//                mIcon11 = BitmapFactory.decodeStream(in);
//            } catch (Exception e) {
//                Log.e("Error", e.getMessage());
//                e.printStackTrace();
//            }
//            return mIcon11;
//        }
//
//        protected void onPostExecute(Bitmap result) {
//            bmImage.setImageBitmap(result);
//        }
//    }

    private class RSSHandler extends DefaultHandler {
        private ArrayList<String> titles, urls;
        private boolean beginArticles, title, firstTitle, link;
        private String fullTitle = "";

        public RSSHandler(ArrayList<String> titles, ArrayList<String> articleLinks) {
            this.titles = titles;
            urls = articleLinks;
        }

        public void characters(char[] ch, int start, int length) throws SAXException {
            String cdata = new String(ch, start, length);
            if (beginArticles && title && firstTitle) {
                fullTitle += cdata.trim();
            } else if (beginArticles && link) {
                urls.add(cdata.trim());
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
                link = localName.equals("link");
//                if (localName.equals("thumbnail")) {
//                    String thumbUrl = attrs.getValue("url");
//                    String urls[] = new String[1];
//                    urls[0] = thumbUrl;
//                    new DownloadImageTask(img)
//                            .execute(urls);
//                }
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
                ArrayList<String> articleLinks = new ArrayList<String>();
                SAXParserFactory factory = SAXParserFactory.newInstance();
                SAXParser saxParser = factory.newSAXParser();
                XMLReader xmlReader = saxParser.getXMLReader();

                RSSHandler rssHandler = new RSSHandler(titles, articleLinks);
                xmlReader.setContentHandler(rssHandler);
                InputSource inputSource = new InputSource(urls[0].openStream());
                xmlReader.parse(inputSource);

                //hacky lol
                final ArrayList<String> tempTitles = titles;
                final ArrayList<String> tempArticleLinks = articleLinks;

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        for(int i = 0; i < tempTitles.size(); ++i) {
                            Button article = new Button(getBaseContext());
                            article.setText(tempTitles.get(i));
                            final int index = i;
                            article.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(getBaseContext(), ArticleClicked.class);
                                    intent.putExtra("url", tempArticleLinks.get(index));
                                    startActivity(intent);
                                }
                            });
                            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            mainView.addView(article, lp);
                        }
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

        mainView = (LinearLayout) findViewById(R.id.linlay);
//        rss = (TextView) findViewById(R.id.rss);
//        img = (ImageView) findViewById(R.id.img);

        try {
            URL rssUrl = new URL("http://time.com/politics/feed/");
            URL[] urls = new URL[1];
            urls[0] = rssUrl;

            DownloadFilesTask task = new DownloadFilesTask();
            task.execute(urls);
        } catch (IOException e) {
            e.printStackTrace();
//            rss.setText(e.getMessage());
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

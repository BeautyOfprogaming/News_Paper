package com.saberi.myenglishnewsagency;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.cursoradapter.widget.SimpleCursorAdapter;
import androidx.loader.content.CursorLoader;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.saberi.myenglishnewsagency.retrofit.DownloadImage;
import com.saberi.myenglishnewsagency.retrofit.models.Article;
import com.saberi.myenglishnewsagency.retrofit.models.Source;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.zip.Inflater;

//import fr.tvbarthel.intentshare.IntentShare;
import retrofit2.http.Url;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class NewItemAdapter extends RecyclerView.Adapter<NewItemAdapter.ItemViewHolder> implements RewardedVideoAdListener {

//    this is a new test

    private Context context;
    private List<Article> items;

    // AdView Section
    private AdRequest adRequest;
    private AdView adView;

    //
    private RewardedVideoAd mRewardedAd;

    private RewardedVideoAd AdMobrewardedVideoAd;
    private String AdId = "ca-app-pub-3940256099942544/5224354917";


    //
    final String PROVIDER_NAME = "androidcontentproviderdemo.androidcontentprovider.images";
    final Uri CONTENT_URI = Uri.parse("content://" + PROVIDER_NAME + "/images");
    ListView listView;
    View view;

    public NewItemAdapter(Context con, List<Article> items) {

        this.context = con;
        this.items = items;

    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {


//        MobileAds.initialize(context,"ca-app-pub-3940256099942544/522435491");
        MobileAds.initialize(context, "ca-app-pub-1981355908194102~2792672342");
        mRewardedAd = MobileAds.getRewardedVideoAdInstance(context);
        mRewardedAd.setRewardedVideoAdListener(this);
        mRewardedAd.loadAd("ca-app-pub-1981355908194102/3324716859", new AdRequest.Builder().build());



//        mRewardedAd.loadAd("cca-app-pub-1981355908194102/1488342268", new AdRequest.Builder().build());
//        loadRewardedVideoAd();


        view = LayoutInflater.from(
                context).inflate(R.layout.news, viewGroup, false);


        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder itemViewHolder, int i) {


        final Article news = items.get(i);

        String pressName = items.get(i).getSource().getId();

        String title = news.getTitle();
        String content = news.getDescription();
        String author = news.getAuthor();
        String date = news.getPublishedAt();
        String url = news.getUrl();
        String imageUrl = news.getUrlToImage();

        itemViewHolder.title.setText(content);
        itemViewHolder.author.setText(author);
        itemViewHolder.content.setText(content);
        itemViewHolder.date.setText(date);
//        itemViewHolder.url.setText(url)
        if (imageUrl != null) {
            itemViewHolder.progressBar.setVisibility(View.VISIBLE);
//            itemViewHolder.imageUrl.setScaleType(ImageView.ScaleType.CENTER);
            Picasso.get().load(imageUrl).into(itemViewHolder.imageUrl, new Callback() {
                @Override
                public void onSuccess() {
                    itemViewHolder.progressBar.setVisibility(View.GONE);

                }

                @Override
                public void onError(Exception e) {

                }
            });


            Log.e("mypicasso_: ", imageUrl);


        }
//         Picasso.get().load(imageUrl).into(itemViewHolder.imageUrl);
        else {
            Picasso.get().load(R.drawable.sajad).into(itemViewHolder.imageUrl);

        }

        itemViewHolder.shareMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {


                    // Set animation to shareButton
                    Animation rotation = AnimationUtils.loadAnimation(context, R.anim.rotation);
                    itemViewHolder.shareMe.startAnimation(rotation);
                    URL url1 = new URL(imageUrl);
                    String host = url1.getHost();
                    String path = url1.getPath();
                    String file = url1.getFile();
                    String fullImageUrl = "www." + host + path + "";


//                     Get access to bitmap image from view
                    ImageView ivImage = (ImageView) view.findViewById(R.id.image);
                    // Get access to the URI for the bitmap
                    Uri bmpUri = getLocalBitmapUri(itemViewHolder.imageUrl);
                    if (bmpUri != null) {
                        // Construct a ShareIntent with link to image
                        Intent shareIntent = new Intent();
                        shareIntent.setAction(Intent.ACTION_SEND);
                        shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
                        shareIntent.putExtra(Intent.EXTRA_TEXT, title + "  " + url);

                        shareIntent.setType("image/*");

                        context.startActivity(Intent.createChooser(shareIntent, "Share Image"));
                    } else {
                        // ...sharing failed, handle error
                    }


                } catch (MalformedURLException e) {
                    Log.e("er0", "er0");
                    e.printStackTrace();
                }

            }


        });

        itemViewHolder.readMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // The below tow lines are for rotating readMore Button
                if(mRewardedAd.isLoaded()){

//                showRewardedVideoAd();
                mRewardedAd.show();

              }

                Animation animation = AnimationUtils.loadAnimation(context, R.anim.read_more_rotation);
                itemViewHolder.readMore.startAnimation(animation);

                Intent intent = new Intent(context, DetailsActivity.class);

                intent.putExtra("url", url);
                intent.putExtra("pressname", pressName);
                context.startActivity(intent);

            }
        });


        adRequest = new AdRequest.Builder().build();
//        itemViewHolder.adView.loadAd(adRequest);
        itemViewHolder.adView.loadAd(adRequest);
        itemViewHolder.adView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


//this is add to show the power of add

    }


    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public void onRewardedVideoAdLoaded() {

    }

    @Override
    public void onRewardedVideoAdOpened() {

    }

    @Override
    public void onRewardedVideoStarted() {

    }

    @Override
    public void onRewardedVideoAdClosed() {

    }

    @Override
    public void onRewarded(RewardItem rewardItem) {

    }

    @Override
    public void onRewardedVideoAdLeftApplication() {

    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {

    }

    @Override
    public void onRewardedVideoCompleted() {

    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView title, content, author, date, url;
        ImageView imageUrl;
        Button readMore, shareMe;
        ListView listView;
        ProgressBar progressBar;

        AdView adView;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            content = itemView.findViewById(R.id.title);
            author = itemView.findViewById(R.id.author);
            date = itemView.findViewById(R.id.date);
//            url = itemView.findViewById(R.id.date);
            imageUrl = itemView.findViewById(R.id.image);

            readMore = itemView.findViewById(R.id.readMore);
            shareMe = itemView.findViewById(R.id.shareMe);
            progressBar = itemView.findViewById(R.id.progress);
            adView = itemView.findViewById(R.id.adView);

            AdRequest adRequest = new AdRequest.Builder().build();
            adView.loadAd(adRequest);

//            return  new ItemViewHolder(itemView);

//            listView = (ListView) itemView.findViewById(R.id.lstViewImages);


        }
    }


    // Returns the URI path to the Bitmap displayed in specified ImageView
    public Uri getLocalBitmapUri(ImageView imageView) {
        // Extract Bitmap from ImageView drawable
        Drawable drawable = imageView.getDrawable();
        Bitmap bmp = null;
        if (drawable instanceof BitmapDrawable) {
            bmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        } else {
            return null;
        }
        // Store image to default external storage directory
        Uri bmpUri = null;
        try {
            // Use methods on Context to access package-specific directories on external storage.
            // This way, you don't need to request external read/write permission.
            // See https://youtu.be/5xVh-7ywKpE?t=25m25s
            File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "share_image_" + System.currentTimeMillis() + ".png");
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            // **Warning:** This will fail for API >= 24, use a FileProvider as shown below instead.


            // wrap File object into a content provider. NOTE: authority here should match authority in manifest declaration
            bmpUri = FileProvider.getUriForFile(context, "com.codepath.fileprovider", file);  // use this version for API >= 24


//            bmpUri = Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }

    // Method when launching drawable within Glide
    public Uri getBitmapFromDrawable(Bitmap bmp) {

        // Store image to default external storage directory
        Uri bmpUri = null;
        try {
            // Use methods on Context to access package-specific directories on external storage.
            // This way, you don't need to request external read/write permission.
            // See https://youtu.be/5xVh-7ywKpE?t=25m25s
            File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "share_image_" + System.currentTimeMillis() + ".png");
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();

            // wrap File object into a content provider. NOTE: authority here should match authority in manifest declaration
            bmpUri = FileProvider.getUriForFile(context, "com.codepath.fileprovider", file);  // use this version for API >= 24

            // **Note:** For API < 24, you may use bmpUri = Uri.fromFile(file);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }


    // added methods


    void loadRewardedVideoAd() {

        // initializing RewardedVideoAd Object

        // RewardedVideoAd Constructor Takes Context as its  Argument

        AdMobrewardedVideoAd = MobileAds.getRewardedVideoAdInstance(context);

        // Loading Rewarded Video Ad

        AdMobrewardedVideoAd.loadAd(AdId, new AdRequest.Builder().build());

    }


    public void showRewardedVideoAd() {


        if (AdMobrewardedVideoAd.isLoaded())//Checking If Ad is Loaded or Not

        {


            // showing Video Ad

            Log.d("ad works", "workssss");
            Toast.makeText(context, "add is showing now", Toast.LENGTH_LONG).show();
            AdMobrewardedVideoAd.show();

        } else {

            // Loading Rewarded Video Ad

            AdMobrewardedVideoAd.loadAd("ca-app-pub-3940256099942544/5224354917", new AdRequest.Builder().build());

            Toast.makeText(context, "add is not showing now", Toast.LENGTH_LONG).show();


        }

    }


}

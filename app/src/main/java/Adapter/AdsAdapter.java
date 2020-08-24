package Adapter;

import android.content.Context;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;

import Models.Ads;
import hcmute.edu.hcmute.AdsActivity;
import hcmute.edu.hcmute.ListAds;
import hcmute.edu.hcmute.R;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static Models.CODE.KEY_ADS;
import static Models.CODE.REQUEST_CODE_ADS;

public class AdsAdapter extends RecyclerView.Adapter<AdsAdapter.ItemViewHolder> {
    private List<Ads> lstAds;
    private Context context;
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    public AdsAdapter(List<Ads> users, Context c) {
        this.lstAds = users;
        this.context = c;
    }

    @Override
    public int getItemCount() {
        return lstAds.size();
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ads, parent, false);

        return new ItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, final int position) {
        final Ads ads = lstAds.get(position);
        Picasso.with(context)
                .load(ads.getUrlImage())
                .into(holder.ivImage);
        holder.tvTitle.setText(ads.getTitle());
        holder.tvContent.setText(ads.getContent());
        holder.tvBody.setText(String.valueOf(ads.getBody()));
        holder.cvItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, AdsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(KEY_ADS, ads);
                intent.putExtras(bundle);
                ((ListAds) context).startActivityForResult(intent, REQUEST_CODE_ADS);
            }
        });

        holder.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lstAds.remove(position);
                deleteAds(ads);
            }
        });
    }

    private void deleteAds(Ads ad) {
        // Khởi tạo OkHttpClient để lấy dữ liệu.
        OkHttpClient client = new OkHttpClient();
        // Tạo request lên server.
        String url = "https://thawing-falls-33830.herokuapp.com/ads/" + ad.get_id();
        Request request = new Request.Builder()
                .url(url)
                .delete()
                .build();
        // Thực thi request.
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("Error", "Network Error");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {

                    ((ListAds) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);

                            builder.setMessage("Delete Successfully!")
                                    .setCancelable(false)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            notifyDataSetChanged();
                                        }
                                    });
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }
                    });

                } else {
                    Log.e("Error Code", String.valueOf(response.code()));
                    //display the appropriate message...
                }
            }
        });
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        public TextView tvTitle;
        public TextView tvBody;
        public TextView tvContent;
        public ImageView ivImage;
        public ImageView ivDelete;
        public CardView cvItem;


        public ItemViewHolder(View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            tvBody = (TextView) itemView.findViewById(R.id.tv_body);
            tvContent = (TextView) itemView.findViewById(R.id.tv_content);
            ivImage = (ImageView) itemView.findViewById(R.id.iv_image);
            ivDelete = (ImageView) itemView.findViewById(R.id.iv_delete);
            cvItem = (CardView) itemView.findViewById(R.id.cv_item);

        }
    }
}
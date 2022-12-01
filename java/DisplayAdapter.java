package com.example.reactive_android;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;

public class DisplayAdapter extends RecyclerView.Adapter<DisplayAdapter.DisplayViewHolder> {

    List<Bitmap> images;
    FirebaseStorage storage;
    StorageReference storageReference;
    int count = 0;

    public DisplayAdapter(List<Bitmap> images, FirebaseStorage storage, StorageReference storageReference)
    {
        this.images = images;
        this.storage = storage;
        this.storageReference = storageReference;
    }

    @NonNull
    @Override
    public DisplayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.single_view,parent,false);
        DisplayViewHolder display_view_holder = new DisplayViewHolder(view);
        return display_view_holder;
    }


    @Override
    public void onBindViewHolder(@NonNull DisplayViewHolder holder, int position) {
        Bitmap image = images.get(position);
        holder.image.setImageBitmap(image);
    }


    @Override
    public int getItemCount() {
        return images.size();
    }


    protected class DisplayViewHolder extends RecyclerView.ViewHolder {

        public ImageView image;

        public DisplayViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.loaded_image);

            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    /* Get ImageView Image In To Bytes */
                    /* Reference : https://stackoverflow.com/questions/29091505/converting-an-image-from-drawable-to-byte-array-in-android */
                    /* Original Source : https://stackoverflow.com/questions/4989182/converting-java-bitmap-to-byte-array/4989543#4989543 */
                    /* Author : Mezm */
                    Bitmap bmp = getBitmap(image.getDrawable());
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    byte[] byteArray = stream.toByteArray();
                    bmp.recycle();

                    /* This is used to uniquley idenify each image sent to storage */
                    String imageNo = String.valueOf(count);
                    String path = "Images/" + imageNo + "/";

                    UploadTask uploadTask = storageReference.child(path).putBytes(byteArray);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Toast.makeText(itemView.getContext(), "Image Uploading Failed!", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(itemView.getContext(), "Image Successfully Uploaded!", Toast.LENGTH_SHORT).show();
                            count++;
                        }
                    });
                }
            });
        }

        /* Reference : https://stackoverflow.com/questions/29091505/converting-an-image-from-drawable-to-byte-array-in-android */
        /* Original Source : https://stackoverflow.com/questions/3035692/how-to-convert-a-drawable-to-a-bitmap/27543712#27543712 */
        /* Author : Chris.Jenkins */
        public Bitmap getBitmap(Drawable drawable) {

            if (drawable instanceof BitmapDrawable) {
                return ((BitmapDrawable) drawable).getBitmap();
            }

            final int width = !drawable.getBounds().isEmpty() ? drawable.getBounds().width() : drawable.getIntrinsicWidth();
            final int height = !drawable.getBounds().isEmpty() ? drawable.getBounds().height() : drawable.getIntrinsicHeight();
            final Bitmap bitmap = Bitmap.createBitmap(width <= 0 ? 1 : width, height <= 0 ? 1 : height, Bitmap.Config.ARGB_8888);

            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        }

    }
}

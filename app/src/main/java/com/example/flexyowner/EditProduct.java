package com.example.flexyowner;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.flexyowner.ControllerClasses.ProductController;
import com.example.flexyowner.ModelClasses.Product;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.io.IOException;
import java.util.UUID;

public class EditProduct extends AppCompatActivity {

    private EditText editTextProductName;
    private EditText editTextProductDescription;
    private EditText editTextProductPrice;
    private Spinner spinnerProductType;
    private FirebaseFirestore db;

    private Uri filePath;
    private final int PICK_IMAGE_REQUEST = 71;
    private Button btnChoose;
    private ImageView imageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);

        editTextProductName = findViewById(R.id.editTextProductName);
        editTextProductDescription = findViewById(R.id.editTextProductDescription);
        editTextProductPrice = findViewById(R.id.editTextProductPrice);
        spinnerProductType = findViewById(R.id.spinnerProductTyoe);


        Spinner spinner = (Spinner) findViewById(R.id.spinnerProductTyoe);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.product_type, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        btnChoose = (Button) findViewById(R.id.btnChooseImage);
        imageView = (ImageView) findViewById(R.id.imgViewProductImage);

        btnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });


        ProductController productController = new ProductController();

        Product product = productController.retrieveCurrentProduct(this);

        editTextProductName.setText(product.getName());
        editTextProductDescription.setText(product.getDescription());
        editTextProductPrice.setText(product.getPrice().toString());
        spinnerProductType.setSelection(adapter.getPosition(product.getType()));
        Glide.with(EditProduct.this).load(product.getImage()).into(imageView);


        db = FirebaseFirestore.getInstance();

        Button buttonEditProduct= findViewById(R.id.buttonEditProduct);

        buttonEditProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Validations
                if (editTextProductName.getText().toString().trim().equals("")) {
                    Toast.makeText(EditProduct.this, "Please enter product name.", Toast.LENGTH_SHORT).show();

                } else if (editTextProductDescription.getText().toString().trim().equals("")) {
                    Toast.makeText(EditProduct.this, "Please enter product description.", Toast.LENGTH_SHORT).show();

                } else {
                    uploadImage(product);
                }
            }
        });

    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imageView.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private void uploadImage(Product product) {

        FirebaseStorage storage;
        StorageReference storageReference;

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();


        if(filePath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference ref = storageReference.child("images/"+ UUID.randomUUID().toString());
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();

                            // Delete previous image

                            StorageReference oldImageRef = storage.getReferenceFromUrl(product.getImage());
                            oldImageRef.delete();

                            // Get url from the nem uploaded image and call update product

                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Uri downloadUrl = uri;
                                    updateProduct(product, downloadUrl.toString());
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();

                            Toast.makeText(EditProduct.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });
        }else{
            updateProduct(product, product.getImage());
        }
    }

    public void updateProduct(Product product, String imageURL){

        final KProgressHUD hud = KProgressHUD.create(EditProduct.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setDetailsLabel("Updating Product")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();


        String txtProductName = ((EditText)(findViewById(R.id.editTextProductName))).getText().toString();
        String txtProductDescription = ((EditText)(findViewById(R.id.editTextProductDescription))).getText().toString();
        Long lngProductPrice = Long.parseLong(((EditText)(findViewById(R.id.editTextProductPrice))).getText().toString());
        String txtProductImage = imageURL;
        String txtProductType = ((Spinner)(findViewById(R.id.spinnerProductTyoe))).getSelectedItem().toString();

        DocumentReference productRef = db.collection("products").document(product.getId());

        productRef
                .update("name",txtProductName,
                        "description",txtProductDescription,
                        "price",lngProductPrice,
                        "image",txtProductImage,
                        "type",txtProductType
                )
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(EditProduct.this, "Product Updated !",
                                Toast.LENGTH_SHORT).show();

                        hud.dismiss();
                        finish();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EditProduct.this, "Error !",
                                Toast.LENGTH_SHORT).show();
                    }
                });

    }

}

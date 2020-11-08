package com.example.flexyowner;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.flexyowner.ControllerClasses.BusinessOwnerController;
import com.example.flexyowner.ModelClasses.Business;
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

public class AddProduct extends AppCompatActivity {

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
        setContentView(R.layout.activity_add_product);

        editTextProductName = findViewById(R.id.editTextProductName);
        editTextProductDescription = findViewById(R.id.editTextProductDescription);
        editTextProductPrice = findViewById(R.id.editTextProductPrice);
        spinnerProductType = findViewById(R.id.spinnerProductTyoe);

        db = FirebaseFirestore.getInstance();

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


        Button buttonAddProduct= findViewById(R.id.buttonAddProduct);

        buttonAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Validations
                if (editTextProductName.getText().toString().trim().equals("")) {
                    Toast.makeText(AddProduct.this, "Please enter product name.", Toast.LENGTH_SHORT).show();

                } else if (editTextProductDescription.getText().toString().trim().equals("")) {
                    Toast.makeText(AddProduct.this, "Please enter product description.", Toast.LENGTH_SHORT).show();

                } else if (editTextProductPrice.getText().toString().trim().equals("")) {
                    Toast.makeText(AddProduct.this, "Please enter product price.", Toast.LENGTH_SHORT).show();

                } else {
                    uploadImage();
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

    private void uploadImage() {

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

                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Uri downloadUrl = uri;
                                    createProduct(downloadUrl.toString());
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();

                            Toast.makeText(AddProduct.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
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
        }
    }


    private void createProduct(String imageURL) {

        BusinessOwnerController businessOwnerController = new BusinessOwnerController();
        Business business = businessOwnerController.retrieveCurrentBusiness(AddProduct.this);

        Product newProduct = new Product(null,
                business.getId(),
                editTextProductName.getText().toString().trim(),
                editTextProductDescription.getText().toString().trim(),
                Long.parseLong(editTextProductPrice.getText().toString().trim()),
                imageURL,
                spinnerProductType.getSelectedItem().toString()
        );

        //Start HUD
        final KProgressHUD hud = KProgressHUD.create(AddProduct.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setDetailsLabel("Inserting Product")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();

        db.collection("products")
                .add(newProduct)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(getPackageName(), "DocumentSnapshot added with ID: " + documentReference.getId());
                        documentReference.update("id",documentReference.getId());
                        hud.dismiss();
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(getPackageName(), "Error adding document", e);
                    }
                });

    }

}

package com.example.basic;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.DefaultGasProvider;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "LandRegistration";
    private static final String INFURA_URL = "https://sepolia.infura.io/v3/210a4da7c70f4e6a88243d788f3c890b";
    private static final String PRIVATE_KEY = "e0b3e5cdaa5865ac15fd32712e73224dbbdc82074ee6eb9210bef82d8084fdea";
    private static final String CONTRACT_ADDRESS = "0x83F3911ae27270ecC024C2E344Fd742DB5ADC828";

    private Web3j web3j;
    private Try6 contract;
    private TextView tvContractResult;
    private EditText etLandName, etBuyerAddress, etBuildingType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize web3j
        web3j = Web3j.build(new HttpService(INFURA_URL));

        // Load contract instance
        loadContract();

        // Initialize Views
        tvContractResult = findViewById(R.id.tvContractResult);
        etLandName = findViewById(R.id.etLandName);
        etBuyerAddress = findViewById(R.id.etBuyerAddress);
        etBuildingType = findViewById(R.id.etBuildingType);

        // Setup buttons
        setupButtons();
    }

    private void loadContract() {
        Credentials credentials = Credentials.create(PRIVATE_KEY);
        ContractGasProvider gasProvider = new DefaultGasProvider();
        contract = Try6.load(CONTRACT_ADDRESS, web3j, credentials, gasProvider);
    }

    private void setupButtons() {
        // Button to call enter function
        Button btnEnter = findViewById(R.id.btnEnter);
        btnEnter.setOnClickListener(v -> enterLand());

        // Button to call show function
        Button btnShow = findViewById(R.id.btnShow);
        btnShow.setOnClickListener(v -> showLand());

        // Button to call transfer function
        Button btnTransfer = findViewById(R.id.btnTransfer);
        btnTransfer.setOnClickListener(v -> transferLand());

        // Button to call history function
        Button btnHistory = findViewById(R.id.btnHistory);
        btnHistory.setOnClickListener(v -> viewHistory());

        // Button to call construction_approval function
        Button btnConstructionApproval = findViewById(R.id.btnConstructionApproval);
        btnConstructionApproval.setOnClickListener(v -> approveConstruction());
    }

    // Function to handle enter function call
    private void enterLand() {
        String landName = etLandName.getText().toString().trim();
        AsyncTask.execute(() -> {
            try {
                Log.d(TAG, "Entering land: " + landName);
                contract.enter(landName).send();
                runOnUiThread(() -> tvContractResult.setText("Land entered successfully"));
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> tvContractResult.setText("Error: " + e.getMessage()));
            }
        });
    }

    // Function to handle show function call
    private void showLand() {
        AsyncTask.execute(() -> {
            try {
                String landName = contract.show().send();
                runOnUiThread(() -> tvContractResult.setText("Your land: " + landName));
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> tvContractResult.setText("Error: " + e.getMessage()));
            }
        });
    }

    // Function to handle transfer function call
    private void transferLand() {
        String buyerAddress = etBuyerAddress.getText().toString().trim();
        AsyncTask.execute(() -> {
            try {
                Log.d(TAG, "Transferring land to: " + buyerAddress);
                contract.transfer(buyerAddress).send();
                runOnUiThread(() -> tvContractResult.setText("Land transferred successfully to " + buyerAddress));
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> tvContractResult.setText("Error: " + e.getMessage()));
            }
        });
    }

    // Function to handle history function call
    private void viewHistory() {
        String landName = etLandName.getText().toString().trim();
        AsyncTask.execute(() -> {
            try {
                Log.d(TAG, "Viewing history for land: " + landName);
                String landHistory = contract.history(landName).send();
                runOnUiThread(() -> tvContractResult.setText("Land History:\n" + landHistory));
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> tvContractResult.setText("Error: " + e.getMessage()));
            }
        });
    }

    // Function to handle construction_approval function call
    private void approveConstruction() {
        String buildingType = etBuildingType.getText().toString().trim();

        AsyncTask.execute(() -> {
            try {
                // Split buildingType input by comma and trim spaces
                final String[] inputs = buildingType.split(",");
                final String finalLandName;
                final String finalBuildingType;

                if (inputs.length > 1) {
                    finalLandName = inputs[0].trim();
                    finalBuildingType = inputs[1].trim();
                } else {
                    finalLandName = etLandName.getText().toString().trim();
                    finalBuildingType = buildingType;
                }

                Log.d(TAG, "Approving construction for land: " + finalLandName + " with building type: " + finalBuildingType);
                String approvalStatus = contract.construction_approval(finalLandName, finalBuildingType).send();
                runOnUiThread(() -> tvContractResult.setText("Construction Approval:\n" + approvalStatus));
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> tvContractResult.setText("Error: " + e.getMessage()));
            }
        });
    }
}

package com.example.predict;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class SuggestFragment extends Fragment {

    // Define UI elements
    private EditText soilMoistureEditText;
    private EditText userRainfallEditText;
    private EditText waterRequirementsEditText;
    private Button calculateButton;
    private TextView waterRequirementResultTextView; // Corrected variable name

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_suggest, container, false);

        // Initialize UI elements
        soilMoistureEditText = rootView.findViewById(R.id.soil_moisture_edit_text);
        userRainfallEditText = rootView.findViewById(R.id.user_rainfall_edit_text);
        waterRequirementsEditText = rootView.findViewById(R.id.water_requirements_edit_text);
        calculateButton = rootView.findViewById(R.id.calculate_button);
        waterRequirementResultTextView = rootView.findViewById(R.id.water_requirement_result_text_view); // Corrected ID

        // Set OnClickListener for the calculate button
        calculateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateWaterRequirements();
            }
        });

        return rootView;
    }

    private void calculateWaterRequirements() {
        // Retrieve input values
        double soilMoisture = Double.parseDouble(soilMoistureEditText.getText().toString());
        double userRainfall = Double.parseDouble(userRainfallEditText.getText().toString());
        double waterRequirements = Double.parseDouble(waterRequirementsEditText.getText().toString());

        // Calculate adjusted water requirements
        double adjustedWaterRequirements = waterRequirements - soilMoisture - userRainfall;

        // Display the result
        waterRequirementResultTextView.setText("Adjusted water requirements: " + adjustedWaterRequirements + " mm per month");

        // Log the result
        Log.d("SuggestFragment", "Adjusted water requirements: " + adjustedWaterRequirements);
    }
}

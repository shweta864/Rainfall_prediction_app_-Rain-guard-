package com.example.predict;

import android.content.res.AssetFileDescriptor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
public class HomeFragment extends Fragment {

    private EditText yearEditText, monthEditText;
    private Button predictButton, refreshButton; // Add refreshButton
    private TextView predictionTextView;
    private Spinner stateSpinner;

    private Interpreter tflite;

    private String[] states = {"East Madhya Pradesh", "East Rajasthan", "Gujarat Region", "Madhya Maharashtra"};
    private String[] modelNames = {"rainfall_prediction_EAST_MADHYA_PRADESH.tflite",
            "rainfall_prediction_EAST_RAJASTHAN.tflite",
            "rainfall_prediction_GUJARAT_REGION.tflite",
            "rainfall_prediction_MADHYA_MAHARASHTRA.tflite"};

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        yearEditText = view.findViewById(R.id.yearEditText);
        monthEditText = view.findViewById(R.id.monthEditText);
        predictButton = view.findViewById(R.id.predictButton);
        refreshButton = view.findViewById(R.id.refreshButton); // Initialize refreshButton
        predictionTextView = view.findViewById(R.id.predictionTextView);
        stateSpinner = view.findViewById(R.id.stateSpinner);

        // Set up the spinner with states
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, states);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stateSpinner.setAdapter(adapter);

        try {
            // Load the default TensorFlow Lite model
            loadModel(modelNames[0]); // Load the model for the first state initially
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error loading the model", Toast.LENGTH_SHORT).show();
        }

        stateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String modelName = modelNames[position]; // Get the corresponding model name for the selected state

                try {
                    // Load the TensorFlow Lite model corresponding to the selected state
                    loadModel(modelName);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Error loading the model", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        predictButton.setOnClickListener(v -> {
            // Get user input
            String yearString = yearEditText.getText().toString();
            String monthString = monthEditText.getText().toString();

            if (!yearString.isEmpty() && !monthString.isEmpty()) {
                try {
                    int year = Integer.parseInt(yearString);
                    int month = Integer.parseInt(monthString);

                    // Validate input for year and month
                    if ((year > 9999  &&  year <= 0) && (month > 5 && month <= 9)) {
                        Toast.makeText(getContext(), "Please enter valid year (<= 9999 && > 0) and month (<= 9 && > 5)", Toast.LENGTH_SHORT).show();
                        return; // Exit the method if input is invalid
                    }

                    // Predict rainfall
                    double predictedRainfall = predictRainfall(year, month) ;

                    // Display the prediction
                    predictionTextView.setText("Predicted Rainfall: " + predictedRainfall + " mm");
                } catch (NumberFormatException e) {
                    // Handle the case where the input is not a valid integer
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Invalid input. Please enter valid numbers", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Handle empty input
                Toast.makeText(getContext(), "Please enter both year and month", Toast.LENGTH_SHORT).show();
            }
        });

        // Add OnClickListener for the refreshButton
        refreshButton.setOnClickListener(v -> {
            // Clear the input text boxes
            yearEditText.setText("");
            monthEditText.setText("");
        });

        return view;
    }

    // Remaining methods...

    private void loadModel(String modelName) throws IOException {
        AssetFileDescriptor fileDescriptor = getResources().getAssets().openFd(modelName);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        MappedByteBuffer modelBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
        tflite = new Interpreter(modelBuffer);
    }

    private double predictRainfall(int year, int month) {
        float[][] input = preprocessInput(year, month);
        float[][] output = new float[1][1];
        tflite.run(input, output);
        return output[0][0];
    }

    private float[][] preprocessInput(int year, int month) {
        float[][] input = new float[1][2];
        input[0][0] = normalizeYear(year);
        input[0][1] = normalizeMonth(month);
        return input;
    }

    private float normalizeYear(int year) {
        // Implement normalization logic for the year feature
        // Replace this with your actual normalization logic
        return (float) year / 1000.0f;
    }

    private float normalizeMonth(int month) {
        // Implement normalization logic for the month feature
        // Replace this with your actual normalization logic
        return (float) month / 12.0f;
    }
}

package com.example.pop3.ui

import android.app.Activity.RESULT_OK
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.NavHostFragment
import com.example.pop3.R
import com.example.pop3.databinding.FragmentCameraBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnSuccessListener
import java.io.ByteArrayOutputStream


class CameraFragment : Fragment(R.layout.fragment_camera) {

    private lateinit var binding: FragmentCameraBinding
    companion object {
        private const val REQUEST_IMAGE_CAPTURE = 1
        private const val REQUEST_LOCATION_PERMISSION = 1
    }
    private var currentLatitude: Double = 0.0
    private var currentLongitude: Double = 0.0
    private var bitmap: Bitmap? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCameraBinding.bind(view)
        Log.d("CameraFragment", "onViewCreated")

        // Solicitar permisos de ubicación
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Si ya se concedieron los permisos, obtén la ubicación
            // Usamos FusedLocationProviderClient para obtener la ubicación actual
            val fusedLocationClient: FusedLocationProviderClient =
                LocationServices.getFusedLocationProviderClient(requireContext())

            fusedLocationClient.lastLocation
                .addOnSuccessListener(requireActivity(), OnSuccessListener { location ->
                    // Almacenamos los valores de latitud y longitud
                    currentLatitude = location.latitude
                    currentLongitude = location.longitude
                    Log.d("CameraFragment", "Latitud: $currentLatitude, Longitud: $currentLongitude")
                    if (location != null) {
                        // Actualizar la interfaz con la ubicación obtenida
                        updateInterfaceWithLocationData(location.latitude, location.longitude)
                    }
                })
        } else {
            // Si no se concedieron, solicitar permisos
            requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_LOCATION_PERMISSION)
        }

        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        } catch (e: ActivityNotFoundException){
            Toast.makeText(requireContext(), "Didn't found any app to take photo", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateInterfaceWithLocationData(latitude: Double, longitude: Double) {
        val locationText = "Latitud: $latitude, Longitud: $longitude"
        binding.imageData.text = locationText
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(requireActivity().packageManager) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val extras: Bundle? = data?.extras
            val imageBitmap = extras?.get("data") as? Bitmap
            // Agregar valores de latitud y longitud al Bundle
            val bundle = Bundle().apply {
                putDouble("latitude", currentLatitude)
                putDouble("longitude", currentLongitude)
            }
            binding.imageAddPhoto.setImageBitmap(imageBitmap)
            binding.imageData.text = "Latitud: $currentLatitude, Longitud: $currentLongitude"
            bitmap = imageBitmap
            // Navegar al HomeFragment con la imagen y datos de ubicación
            binding.buttonSavePhoto.setOnClickListener {
                saveImageToGallery(imageBitmap, bundle)
                showToast("Image saved to gallery")
                navigateToHomeFragment(bundle)
            }
            binding.buttonDiscardPhoto.setOnClickListener {
                return@setOnClickListener
            }
        }
    }

    private fun navigateToHomeFragment(bundle: Bundle) {
        NavHostFragment.findNavController(this).navigate(R.id.action_cameraFragment_to_homeFragment, bundle)
    }

    private fun saveImageToGallery(imageBitmap: Bitmap?, bundle: Bundle) {
        imageBitmap?.let {
            val encodedImage = encodeBitmapToBase64(imageBitmap)
            val sharedPreferences = requireActivity().getPreferences(Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString("encodedImage", encodedImage)
            editor.apply()
        }
    }

    private fun encodeBitmapToBase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }


}
package com.example.pop3.ui

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.pop3.R
import com.example.pop3.data.RetrofitClient
import com.example.pop3.data.WebService
import com.example.pop3.databinding.FragmentHomeBinding
import com.example.pop3.models.ImageData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var binding: FragmentHomeBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHomeBinding.bind(view)
        loadLastImage()
        binding.buttonMintNFT.setOnClickListener {
            val url = "https://popchainlink.netlify.app/"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        }
    }

    private fun loadLastImage() {
        val sharedPreferences = requireActivity().getSharedPreferences("pop3", Context.MODE_PRIVATE)
        val encodedImage = sharedPreferences.getString("encodedImage", null)
        encodedImage?.let {
            val bitmap = decodeBase64ToBitmap(encodedImage)
            binding.imageViewLastImage.setImageBitmap(bitmap)
        }
    }

    private fun initViews() {
        binding.buttonMintNFT.setOnClickListener {
            sendImageDataToServer("image", "attributes")
        }
    }

    private fun decodeBase64ToBitmap(encodedImage: String): Bitmap {
        val decodedByteArray = Base64.decode(encodedImage, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.size)
    }

    private fun sendImageDataToServer(image: String, attributes: String) {
        val webServer = RetrofitClient.getRetrofitInstance().create(WebService::class.java)
        val imageData = ImageData(image, attributes)
        val call = webServer.sendImageData(imageData)

        call.enqueue(object : Callback<Void>{
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    // La solicitud se completó exitosamente
                    // Puedes realizar acciones adicionales aquí si es necesario
                } else {
                    // La solicitud no fue exitosa, manejar el error si es necesario
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(requireContext(), "Error sending image to server", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
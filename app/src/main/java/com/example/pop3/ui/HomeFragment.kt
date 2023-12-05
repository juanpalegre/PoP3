package com.example.pop3.ui

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.pop3.R
import com.example.pop3.databinding.FragmentHomeBinding


class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var binding: FragmentHomeBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHomeBinding.bind(view)
        initViews()
        loadLastImage()
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
            Toast.makeText(requireContext(), "Mint NFT", Toast.LENGTH_SHORT).show()
        }
    }

    private fun decodeBase64ToBitmap(encodedImage: String): Bitmap {
        val decodedByteArray = Base64.decode(encodedImage, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.size)
    }




}
package com.capstone.signora.ui.frontend.tutorial

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.capstone.signora.R
import com.capstone.signora.databinding.FragmentTutorialBinding

class TutorialFragment : Fragment() {
    private var imageResource = 0
    private lateinit var image: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            imageResource = requireArguments().getInt(ARG_IMAGE_RESOURCE)
        }
    }

    private var _binding: FragmentTutorialBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTutorialBinding.inflate(inflater, container, false)
        val view = binding.root
        image = binding.imageTutorial
        image.setImageResource(imageResource)
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_IMAGE_RESOURCE = "image_resource"
        fun newInstance(imageResource: Int): TutorialFragment {
            val fragment = TutorialFragment()
            val args = Bundle()
            args.putInt(ARG_IMAGE_RESOURCE, imageResource)
            fragment.arguments = args
            return fragment
        }
    }
}

package com.capstone.signora

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import android.widget.ImageView
import android.view.WindowManager

class FullScreenImageDialogFragment : DialogFragment() {

    companion object {
        private const val ARG_IMAGE_URL = "image_url"

        fun newInstance(imageUrl: String): FullScreenImageDialogFragment {
            val fragment = FullScreenImageDialogFragment()
            val args = Bundle()
            args.putString(ARG_IMAGE_URL, imageUrl)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_full_screen_image_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val imageUrl = arguments?.getString(ARG_IMAGE_URL)
        val imageView = view.findViewById<ImageView>(R.id.fullScreenImageView)
        if (imageUrl != null) {
            Glide.with(this)
                .load(imageUrl)
                .error(R.drawable.ic_error)
                .into(imageView)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
        dialog.window?.setBackgroundDrawableResource(android.R.color.black)
        return dialog
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
    }
}

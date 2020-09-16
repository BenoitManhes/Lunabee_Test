package com.example.lunabeeusers.ui.detail

import android.os.Bundle
import android.transition.TransitionInflater
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.lunabeeusers.R
import com.example.lunabeeusers.data.model.User

import com.example.lunabeeusers.databinding.DetailFragmentBinding
import com.example.lunabeeusers.utils.avatarTransitionName
import com.example.lunabeeusers.utils.loadCircleImageFromUrl
import com.example.lunabeeusers.utils.nameTransitionName
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class DetailFragment : Fragment() {

    // View Model
    private val viewModel: DetailViewModel by viewModels()

    // Binding
    private lateinit var binding: DetailFragmentBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        binding = DetailFragmentBinding.inflate(inflater)

        // Share element transition
        sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
        postponeEnterTransition(250, TimeUnit.MILLISECONDS)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setting current user in viewModel
        val arguments = DetailFragmentArgs.fromBundle(requireArguments())
        viewModel.setCurrentUser(arguments.user)
        setupAnimation()
        setupObservers()

        // Define Shared Elements
        binding.userAvatarIv.transitionName = avatarTransitionName(arguments.user)
        binding.nameTv.transitionName = nameTransitionName(arguments.user)
    }

    private fun setupAnimation() {
        val slideInBottomSeparator = AnimationUtils.loadAnimation(context, R.anim.slide_in_bottom)
        binding.separator.startAnimation(slideInBottomSeparator)

        val slideInBottomMail = AnimationUtils.loadAnimation(context, R.anim.slide_in_bottom2)
        binding.emailIv.startAnimation(slideInBottomMail)
        binding.emailTv.startAnimation(slideInBottomMail)

        val slideInBottomGenre = AnimationUtils.loadAnimation(context, R.anim.slide_in_bottom3)
        binding.genreIv.startAnimation(slideInBottomGenre)
        binding.genreTv.startAnimation(slideInBottomGenre)

    }

    private fun setupObservers() {
        viewModel.currentUser.observe(viewLifecycleOwner, Observer {
            it?.let {
                bindUser(it)
            }
        })
    }

    private fun bindUser(user: User) {
        binding.nameTv.text = user.firstname + " " + user.lastname
        binding.emailTv.text = user.email
        binding.genreTv.text = user.gender
        binding.userAvatarIv.loadCircleImageFromUrl(user.imgSrcUrl)
    }

}

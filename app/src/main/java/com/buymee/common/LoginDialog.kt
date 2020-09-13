package com.buymee.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import com.buymee.R
import kotlinx.android.synthetic.main.login_layout.*


class LoginDialog : DialogFragment() {

    interface LoginDialogListener {
        fun onSignInClick(email: String, password: String)
        fun onShowRegisterClick()
    }

    private var listener: LoginDialogListener? = null

    companion object {
        fun newInstance(): LoginDialog {
            return LoginDialog()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.login_layout, container)
        dialog?.window?.setDimAmount(0.2F)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        email.editText!!.addTextChangedListener {
            if (email.editText!!.text.isNotEmpty()) {
                email.error = null
            }
        }
        password.editText!!.addTextChangedListener {
            if (password.editText!!.text.isNotEmpty()) {
                password.error = null
            }
        }
        sign_in.setOnClickListener {
            if (email.editText!!.text.isEmpty()) {
                email.error = "Please write your email account"
            } else {
                email.error = null
            }
            if (password.editText!!.text.isEmpty()) {
                password.error = "Please write your password"
            } else {
                password.error = null
            }
            if (email.error == null && password.error == null) {
                listener?.onSignInClick(
                    email.editText!!.text.toString(),
                    password.editText!!.text.toString()
                )
            }
        }

        show_register.setOnClickListener {
            listener?.onShowRegisterClick()
        }
    }

    fun setLoginDialogListener(loginDialogListener: LoginDialogListener) {
        this.listener = loginDialogListener
    }
}
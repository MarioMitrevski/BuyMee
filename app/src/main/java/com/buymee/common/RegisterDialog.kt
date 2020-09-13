package com.buymee.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import com.buymee.R
import kotlinx.android.synthetic.main.register_layout.*

class RegisterDialog : DialogFragment() {

    interface RegisterDialogListener {
        fun onRegisterClick(firstName: String, lastName: String, email: String, password: String)
        fun onShowSignInClick()
    }

    private var listener: RegisterDialogListener? = null

    companion object {
        fun newInstance(): RegisterDialog {
            return RegisterDialog()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.register_layout, container)
        dialog?.window?.setDimAmount(0.2F)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        first_name.editText!!.addTextChangedListener {
            if (first_name.editText!!.text.isNotEmpty()) {
                first_name.error = null
            }
        }
        last_name.editText!!.addTextChangedListener {
            if (last_name.editText!!.text.isNotEmpty()) {
                last_name.error = null
            }
        }
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
        register.setOnClickListener {
            if (first_name.editText!!.text.isEmpty()) {
                first_name.error = "Please write your name"
            } else {
                first_name.error = null
            }
            if (last_name.editText!!.text.isEmpty()) {
                last_name.error = "Please write your surname"
            } else {
                last_name.error = null
            }
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
            if (email.error == null && password.error == null && first_name.error == null && last_name.error == null) {
                listener?.onRegisterClick(
                    first_name.editText!!.text.toString(),
                    last_name.editText!!.text.toString(),
                    email.editText!!.text.toString(),
                    password.editText!!.text.toString()
                )
            }
        }

        show_sign_in.setOnClickListener {
            listener?.onShowSignInClick()
        }
    }

    fun setRegisterDialogListener(registerDialogListener: RegisterDialogListener) {
        this.listener = registerDialogListener
    }
}
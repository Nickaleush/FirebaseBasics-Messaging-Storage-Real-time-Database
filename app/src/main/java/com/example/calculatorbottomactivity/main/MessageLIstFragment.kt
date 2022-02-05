package com.example.calculatorbottomactivity.main
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.calculatorbottomactivity.R
import com.example.calculatorbottomactivity.main.MainActivity
import kotlinx.android.synthetic.main.fragment_messagelist.*

class MessageLIstFragment : Fragment() {
    public var counter  =0;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_messagelist, container, false)
        return rootView
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Send_data.setOnClickListener {
            if (activity != null) {
                (activity as MainActivity).saveInfo()
            }
        }
        show_msg.setOnClickListener {
            if (activity != null) {
                (activity as MainActivity).showMessage()
            }
        }

    }
}
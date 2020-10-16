package com.example.userpagetablayout.fragments.adapters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.userpagetablayout.R
import kotlinx.android.synthetic.main.fragment_home.*


class ViewPagerAdapter(fragment: Fragment) :
    FragmentStateAdapter(fragment) {


    private val mFragmentList = ArrayList<Fragment>()
    private val mFragmentTitleList = ArrayList<String>()
    override fun getItemCount(): Int {
        return mFragmentList.size

    }

    override fun createFragment(position: Int): Fragment {
        val fragment = mFragmentList[position]
        return fragment
    }

    fun addFragment(fragment: Fragment, title: String) {
        mFragmentList.add(fragment)
        mFragmentTitleList.add(title)
    }
}
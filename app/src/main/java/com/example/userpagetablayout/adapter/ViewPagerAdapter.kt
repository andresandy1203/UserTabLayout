package com.example.userpagetablayout.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter


class ViewPagerAdapter(fragment: Fragment) :
    FragmentStateAdapter(fragment) {

    //Initiate the Fragment List with its respective title list
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
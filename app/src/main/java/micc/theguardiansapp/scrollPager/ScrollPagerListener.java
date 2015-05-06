package micc.theguardiansapp.scrollPager;

/**
 * Created by nagash on 01/05/15.
 */
public interface ScrollPagerListener {
    //public void onPageChanged(int oldPage, int oldFragment, int newPage, int newFragment);

    public void onFragmentChanged(int oldFragment, int newFragment);
    public void onPageChanged(int oldPage, int newPage, int oldFragment, int newFragment);
    public void onPagerGuiInit();
}

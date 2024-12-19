package top.mrxiaom.sweet.drops.func.entry;

public class RoundCeil implements IRound {
    public static final RoundCeil INSTANCE = new RoundCeil();
    private RoundCeil() {}
    @Override
    public int process(double num) {
        return (int) Math.ceil(num);
    }
}

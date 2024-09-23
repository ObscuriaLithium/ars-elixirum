package dev.obscuria.elixirum.common;

final class EasingFunctions {

    public static Easing linear() {
        return progress -> progress;
    }

    public static Easing ceil() {
        return progress -> 1;
    }

    public static Easing floor() {
        return progress -> 0;
    }

    public static Easing easeInSine() {
        return progress -> 1f - (float) Math.cos((progress * Math.PI) / 2f);
    }

    public static Easing easeInCircle() {
        return progress -> 1f - (float) Math.sqrt(1f - Math.pow(progress, 2f));
    }

    public static Easing easeInQuad() {
        return progress -> (float) Math.pow(progress, 2);
    }

    public static Easing easeInCubic() {
        return progress -> (float) Math.pow(progress, 3);
    }

    public static Easing easeInQuart() {
        return progress -> (float) Math.pow(progress, 4);
    }

    public static Easing easeInQuint() {
        return progress -> (float) Math.pow(progress, 5);
    }

    public static Easing easeInExpo() {
        return progress -> progress == 0 ? 0 : (float) Math.pow(2f, 10f * progress - 10f);
    }

    public static Easing easeInBack() {
        return progress -> 2.70158f * progress * progress * progress - 1.70158f * progress * progress;
    }

    public static Easing easeInElastic() {
        return progress -> progress == 0 ? 0 : (progress == 1 ? 1 : (float) (-Math.pow(2f, 10f * progress - 10f) * Math.sin((progress * 10f - 10.75f) * ((2f * Math.PI) / 3f))));
    }

    public static Easing easeInBounce() {
        return progress -> 1f - easeOutBounce().get(1f - progress);
    }

    public static Easing easeOutSine() {
        return progress -> (float) Math.sin((progress * Math.PI) / 2f);
    }

    public static Easing easeOutCircle() {
        return progress -> (float) Math.sqrt(1f - Math.pow(progress - 1f, 2f));
    }

    public static Easing easeOutQuad() {
        return progress -> 1f - (float) Math.pow(progress - 1f, 2);
    }

    public static Easing easeOutCubic() {
        return progress -> 1f + (float) Math.pow(progress - 1f, 3);
    }

    public static Easing easeOutQuart() {
        return progress -> 1f - (float) Math.pow(progress - 1f, 4);
    }

    public static Easing easeOutQuint() {
        return progress -> 1f + (float) Math.pow(progress - 1f, 5);
    }

    public static Easing easeOutExpo() {
        return progress -> progress == 1 ? 1 : 1f - (float) Math.pow(2f, -10f * progress);
    }

    public static Easing easeOutBack() {
        return progress -> 1f + 2.70158f * (float) Math.pow(progress - 1f, 3f) + 1.70158f * (float) Math.pow(progress - 1f, 2f);
    }

    public static Easing easeOutElastic() {
        return progress -> progress == 0 ? 0 : progress == 1 ? 1 : (float) (Math.pow(2f, -10f * progress) * Math.sin((progress * 10f - 0.75f) * ((2f * Math.PI) / 3f)) + 1f);
    }

    public static Easing easeOutBounce() {
        return progress -> {
            float f1 = 7.5625f;
            float f2 = 2.75f;
            if (progress < 1f / f2) return f1 * progress * progress;
            else if (progress < 2 / f2) return f1 * (progress -= 1.5f / f2) * progress + 0.75f;
            else if (progress < 2.5 / f2) return f1 * (progress -= 2.25f / f2) * progress + 0.9375f;
            else return f1 * (progress -= 2.625f / f2) * progress + 0.984375f;
        };
    }

    public static Easing easeInOutSine() {
        return easeInSine().merge(easeOutSine());
    }

    public static Easing easeInOutCircle() {
        return easeInCircle().merge(easeOutCircle());
    }

    public static Easing easeInOutQuad() {
        return easeInQuad().merge(easeOutQuad());
    }

    public static Easing easeInOutCubic() {
        return easeInCubic().merge(easeOutCubic());
    }

    public static Easing easeInOutQuart() {
        return easeInQuart().merge(easeOutQuart());
    }

    public static Easing easeInOutQuint() {
        return easeInQuint().merge(easeOutQuint());
    }

    public static Easing easeInOutExpo() {
        return easeInExpo().merge(easeOutExpo());
    }

    public static Easing easeInOutBack() {
        return easeInBack().merge(easeOutBack());
    }

    public static Easing easeInOutElastic() {
        return easeInElastic().merge(easeOutElastic());
    }

    public static Easing easeInOutBounce() {
        return easeInBounce().merge(easeOutBounce());
    }

    public static Easing easeOutInSine() {
        return easeOutSine().merge(easeInSine());
    }

    public static Easing easeOutInCircle() {
        return easeOutCircle().merge(easeInCircle());
    }

    public static Easing easeOutInQuad() {
        return easeOutQuad().merge(easeInQuad());
    }

    public static Easing easeOutInCubic() {
        return easeOutCubic().merge(easeInCubic());
    }

    public static Easing easeOutInQuart() {
        return easeOutQuart().merge(easeInQuart());
    }

    public static Easing easeOutInQuint() {
        return easeOutQuint().merge(easeInQuint());
    }

    public static Easing easeOutInExpo() {
        return easeOutExpo().merge(easeInExpo());
    }

    public static Easing easeOutInBack() {
        return easeOutBack().merge(easeInBack());
    }

    public static Easing easeOutInElastic() {
        return easeOutElastic().merge(easeInElastic());
    }

    public static Easing easeOutInBounce() {
        return easeOutBounce().merge(easeInBounce());
    }
}
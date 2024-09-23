package dev.obscuria.elixirum.common;

@SuppressWarnings("unused")
public interface Easing {

    Easing LINEAR = EasingFunctions.linear();
    Easing CEIL = EasingFunctions.ceil();
    Easing FLOOR = EasingFunctions.floor();

    Easing EASE_IN_SINE = EasingFunctions.easeOutSine();
    Easing EASE_IN_CIRCLE = EasingFunctions.easeOutCircle();
    Easing EASE_IN_QUAD = EasingFunctions.easeInQuad();
    Easing EASE_IN_CUBIC = EasingFunctions.easeInCubic();
    Easing EASE_IN_QUART = EasingFunctions.easeInQuart();
    Easing EASE_IN_QUINT = EasingFunctions.easeInQuint();
    Easing EASE_IN_EXPO = EasingFunctions.easeInExpo();
    Easing EASE_IN_BACK = EasingFunctions.easeInBack();
    Easing EASE_IN_ELASTIC = EasingFunctions.easeInElastic();
    Easing EASE_IN_BOUNCE = EasingFunctions.easeInBounce();

    Easing EASE_OUT_SINE = EasingFunctions.easeOutSine();
    Easing EASE_OUT_CIRCLE = EasingFunctions.easeOutCircle();
    Easing EASE_OUT_QUAD = EasingFunctions.easeOutQuad();
    Easing EASE_OUT_CUBIC = EasingFunctions.easeOutCubic();
    Easing EASE_OUT_QUART = EasingFunctions.easeOutQuart();
    Easing EASE_OUT_QUINT = EasingFunctions.easeOutQuint();
    Easing EASE_OUT_EXPO = EasingFunctions.easeOutExpo();
    Easing EASE_OUT_BACK = EasingFunctions.easeOutBack();
    Easing EASE_OUT_ELASTIC = EasingFunctions.easeOutElastic();
    Easing EASE_OUT_BOUNCE = EasingFunctions.easeOutBounce();

    Easing EASE_IN_OUT_SINE = EasingFunctions.easeInOutSine();
    Easing EASE_IN_OUT_CIRCLE = EasingFunctions.easeInOutCircle();
    Easing EASE_IN_OUT_QUAD = EasingFunctions.easeInOutQuad();
    Easing EASE_IN_OUT_CUBIC = EasingFunctions.easeInOutCubic();
    Easing EASE_IN_OUT_QUART = EasingFunctions.easeInOutQuart();
    Easing EASE_IN_OUT_QUINT = EasingFunctions.easeInOutQuint();
    Easing EASE_IN_OUT_EXPO = EasingFunctions.easeInOutExpo();
    Easing EASE_IN_OUT_BACK = EasingFunctions.easeInOutBack();
    Easing EASE_IN_OUT_ELASTIC = EasingFunctions.easeInOutElastic();
    Easing EASE_IN_OUT_BOUNCE = EasingFunctions.easeInOutBounce();

    Easing EASE_OUT_IN_SINE = EasingFunctions.easeOutInSine();
    Easing EASE_OUT_IN_CIRCLE = EasingFunctions.easeOutInCircle();
    Easing EASE_OUT_IN_QUAD = EasingFunctions.easeOutInQuad();
    Easing EASE_OUT_IN_CUBIC = EasingFunctions.easeOutInCubic();
    Easing EASE_OUT_IN_QUART = EasingFunctions.easeOutInQuart();
    Easing EASE_OUT_IN_QUINT = EasingFunctions.easeOutInQuint();
    Easing EASE_OUT_IN_EXPO = EasingFunctions.easeOutInExpo();
    Easing EASE_OUT_IN_BACK = EasingFunctions.easeOutInBack();
    Easing EASE_OUT_IN_ELASTIC = EasingFunctions.easeOutInElastic();
    Easing EASE_OUT_IN_BOUNCE = EasingFunctions.easeOutInBounce();

    float get(float progress);

    default Easing scale(float scale) {
        return progress -> this.get(Math.max(Math.min(progress * (1f / scale), 1f), 0f));
    }

    default Easing reversed() {
        return progress -> 1f - this.get(progress);
    }

    default Easing merge(Easing other) {
        return this.merge(other, 0.5f);
    }

    default Easing merge(Easing other, float ratio) {
        return progress -> progress <= ratio
                ? ratio * this.scale(ratio).get(progress)
                : ratio * this.get(1f) + (1f - ratio) * other.scale(1f - ratio).get(progress - ratio);
    }

    default Easing mergeOut(Easing other) {
        return this.mergeOut(other, 0.5f);
    }

    default Easing mergeOut(Easing other, float ratio) {
        return progress -> progress <= ratio
                ? this.scale(ratio).get(progress)
                : other.reversed().scale(1f - ratio).get(progress - ratio);
    }
}
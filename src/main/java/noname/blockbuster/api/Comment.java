package noname.blockbuster.api;

/**
 * Comment annotation
 *
 * Used to comment very important code snippets so it would be seen in
 * compiled jar. Just <s>for fun</s> in case.
 *
 * By the way, that's is the most nasty workaround.
 */
@Comment(comment = "Haha, recursion was understood, maybe, because I don't know what it does")
public @interface Comment
{
    String comment() default "";

    String author() default "";
}

package mchorse.blockbuster.recording.actions;

/**
 * Logout action
 *
 * Doesn't do anything yet, maybe I'll add some specific behavior, like the
 * cause of log out (griefers, rage quit, etc.)
 */
public class LogoutAction extends Action
{
    public LogoutAction()
    {}

    @Override
    public byte getType()
    {
        return Action.LOGOUT;
    }
}

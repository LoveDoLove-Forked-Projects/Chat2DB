import React, { memo } from 'react';
import { useStyles } from './style';
import UserAvatar from '@/components/UserAvatar';
import Logo from '@/components/Logo';

export interface ChatItemProps {
  className?: string;
  avatar?: 'user';
  type?: 'question' | 'answer';
  // the last message?
  renderMessage: () => React.ReactNode;
  // renders custom tail
  renderFooter?: () => React.ReactNode;
}

export default memo<ChatItemProps>((props) => {
  const { className, renderMessage, renderFooter, avatar, type } = props;
  const { styles, cx } = useStyles();
  const isUser = avatar === 'user';

  const renderAvatar = () => {
    if (isUser) {
      // return null;
      return (
        <div className="avatarBox">
          <UserAvatar size={40} />
        </div>
      );
    }
    return (
      <div className="avatarBox">
        <Logo size={40} className="avatarBoxLogo" />
      </div>
    );
  };

  return (
    <div
      className={cx(
        styles.container,
        { [styles.questionContainer]: type === 'question' },
        { [styles.answerContainer]: type === 'answer' },
        className,
      )}
    >
      {/* {renderAvatar()} */}
      <div className="messageContainer">
        <div className="timeBox"></div>
        <div className="messageContent">
          <div className="messageBox">
            {renderMessage()}
            {renderFooter?.()}
          </div>
        </div>
        {/* {renderFooter?.()} */}
      </div>
    </div>
  );
});

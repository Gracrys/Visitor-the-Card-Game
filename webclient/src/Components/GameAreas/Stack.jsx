import React from "react";
import { connect } from "react-redux";

import PlayingCard from "../Card/PlayingCard";
import "../../css/Stack.css";
import "../../css/Utils.css";
import VerticalStack from '../Primitives/ComponentStack';
import { withSize } from "react-sizeme";

const mapStateToProps = state => {
  return { stack: state.extendedGameState.game.stack };
};

class Stack extends React.Component {
  render() {
    const { stack, size } = this.props;
    return (
      <VerticalStack
        stepSize={size.width / 5}
      >
        {stack.reverse().map((card, i) => {
          return <PlayingCard key={i} cardData={card} />;
        })}
      </VerticalStack>
    );
  }
}

export default connect(mapStateToProps)(withSize()(Stack));

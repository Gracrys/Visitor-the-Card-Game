import React from "react";
import Grid from "@material-ui/core/Grid";
import Button from "../Primitives/Button";
import Center from "react-center";
import { connect } from "react-redux";

import { withFirebase } from "../Firebase";
import DeckBuilder from "./DeckBuilder";
import { delayClick } from "../Helpers/Helpers";
import { mapDispatchToProps } from "../Redux/Store";

const mapStateToProps = state => {
  return { userId: state.firebaseAuthData.user.uid };
};

class Decks extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      value: 0,
      loadedDeck: "",
      selectedDeckId: "",
      decks: []
    };
    this.back = () => {
      this.setState({ loadedDeck: "", selectedDeckId: "", decks: [] });
      const Return = this.addDeck.bind(this);
      this.props.firebase.getAllDecks(this.props.userId, Return);
      this.setState({ value: 0 });
    };
  }

  componentWillMount() {
    this.setState({ loadedDeck: "", selectedDeckId: "", decks: [] });
    const Return = this.addDeck.bind(this);
    this.props.firebase.getAllDecks(this.props.userId, Return);
  }

  selectDeck = deckId => {
    if (deckId === this.state.selectedDeckId) {
      this.setState({ selectedDeckId: "" });
    } else {
      this.setState({ selectedDeckId: deckId });
    }
  };

  loadDeck = deckId => {
    this.setState({ value: 1, loadedDeck: deckId });
  };

  addDeck = deck => {
    this.setState((state, props) => ({ decks: state.decks.concat([deck]) }));
  };

  deleteDeck = () => {
    this.props.firebase.deleteDeck(
      this.props.userId,
      this.state.selectedDeckId
    );
    this.setState((state, props) => ({
      selectedDeckId: "",
      decks: state.decks.filter(item => item.id !== this.state.selectedDeckId)
    }));
  };

  createDeck = () => {
    const { firebase, userId } = this.props;
    const Return = this.loadDeck.bind(this);
    firebase.createNewDeck(userId, "New Deck", Return);
  };

  render() {
    const { value, decks, selectedDeckId, loadedDeck } = this.state;
    return (
      <div>
        {value === 0 && (
          <div>
            <Button onClick={this.props.back} text="Back" />
            <Grid container spacing={8}>
              {decks.map((deck, i) => (
                <Grid
                  item
                  key={i}
                  xs={4}
                  onClick={delayClick(
                    () => this.selectDeck(deck.id),
                    () => this.setState({ value: 1, loadedDeck: deck.id })
                  )}
                >
                  <Center>
                    <img
                      src={process.env.PUBLIC_URL + "/img/Logo.png"}
                      style={{
                        maxWidth: "100%",
                        maxHeight: "100%",
                        opacity: deck.id === selectedDeckId ? 0.5 : 1
                      }}
                      alt=""
                    />
                  </Center>
                  <Center>{deck.name}</Center>
                </Grid>
              ))}
              <Grid container item xs={12} spacing={8}>
                <Grid item xs>
                  <Button onClick={this.createDeck} text="Create" />
                </Grid>
                <Grid item xs>
                  <Button
                    disabled={selectedDeckId === ""}
                    onClick={this.deleteDeck}
                    text="Delete"
                  />
                </Grid>
              </Grid>
            </Grid>
          </div>
        )}
        {value === 1 && <DeckBuilder back={this.back} deckId={loadedDeck} />}
      </div>
    );
  }
}

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(withFirebase(Decks));

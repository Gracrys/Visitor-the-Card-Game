import React, { Component } from "react";
import Dialog from "@material-ui/core/Dialog";
import DialogTitle from "@material-ui/core/DialogTitle";
import DialogContent from "@material-ui/core/DialogContent";
import Grid from "@material-ui/core/Grid";
import Button from "@material-ui/core/Button";
import { connect } from "react-redux";

import PlayingCard from "../Card/PlayingCard";
import { withHandlers } from "../MessageHandlers/HandlerContext";
import { mapDispatchToProps } from "../Redux/Store";
import { ClientPhase } from "../Helpers/Constants";
import { Droppable } from "react-beautiful-dnd";

import "../../css/ChooseDialog.css";
import CardDisplay from "../Card/CardDisplay";

const mapStateToProps = state => {
  return {
    clientPhase: state.extendedGameState.clientPhase,
    selected: state.extendedGameState.selectionData.selected,
    dialog: state.extendedGameState.dialogData,
    upTo: state.extendedGameState.selectionData.upTo,
    message: state.extendedGameState.message,
  };
};

class ChooseDialog extends Component {
  onClose = event => {
    this.props.updateExtendedGameState({
      dialogData: {
        title: "",
        cards: [],
        open: false
      }
    });
  };

  selectDone = event => {
    let selected = [...this.props.selected];
    let clientPhase = this.props.clientPhase;
    this.props.gameHandler.SelectDone(clientPhase, selected);
  };

  render = () => {
    const { clientPhase, dialog, upTo, message } = this.props;

    const isSelectPhase =
      clientPhase === ClientPhase.SELECT_FROM_LIST ||
      clientPhase === ClientPhase.SELECT_FROM_DISCARD_PILE ||
      clientPhase === ClientPhase.SELECT_FROM_VOID;

    return (
      <Dialog
        open={dialog.open}
        onClose={this.onClose}
        maxWidth=false
        fullWidth
        disableBackdropClick={isSelectPhase}
        disableEscapeKeyDown={isSelectPhase}
        scroll="body"
      >
        <DialogTitle> {message} </DialogTitle>
        <DialogContent>
          <Droppable droppableId={"modal-list"} isDropDisabled>
            {provided => (
              <div ref={provided.innerRef} {...provided.droppableProps}>
                <Grid container spacing={0} className="choose-dialog">
                  {dialog.cards.map((card, i) => (
                    <Grid item xs={1} key={card.id}>
                      <PlayingCard
                        cardData={card}
                        isDragDisabled
                        DnDIndex={i}
                        popoverDisabled
                      />
                    </Grid>
                  ))}
                </Grid>
              </div>
            )}
          </Droppable>
          {upTo && (
            <Button
              color="primary"
              variant="contained"
              onClick={this.selectDone}
            >
              Done
            </Button>
          )}
        </DialogContent>
      </Dialog>
    );
  };
}

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(withHandlers(ChooseDialog));

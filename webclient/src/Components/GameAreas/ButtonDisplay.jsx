import React, { Component } from "react";
import Grid from "@material-ui/core/Grid";
import Paper from "@material-ui/core/Paper";
import Button from "../Primitives/Button";
import Textfit from "react-textfit";
import { connect } from "react-redux";

import proto from "../../protojs/compiled.js";

import { ClientPhase } from "../Helpers/Constants";
import { IsSelectCardPhase } from "../Helpers/Helpers";
import { withHandlers } from "../MessageHandlers/HandlerContext";
import "../../css/Utils.css";

const mapStateToProps = state => {
  return {
    gamePhase: state.extendedGameState.game.phase,
    playerName: state.username,
    playerUserId: state.extendedGameState.game.player.userId,
    opponentName: state.extendedGameState.opponentUsername,
    activePlayer: state.extendedGameState.game.activePlayer,
    turnPlayer: state.extendedGameState.game.turnPlayer,
    clientPhase: state.extendedGameState.clientPhase,
    upTo: state.extendedGameState.selectionData.upTo,
    autoPass: state.extendedGameState.autoPass,
    selected: state.extendedGameState.selectionData.selected,
    attackerAssignments: state.extendedGameState.attackerAssignmentData.attackerAssignments,
    blockerAssignments: state.extendedGameState.blockerAssignmentData.blockerAssignments,
  };
};

class ButtonDisplay extends Component {
  redraw = event => {
    this.props.gameHandler.Redraw();
  };

  keep = event => {
    this.props.gameHandler.Keep();
  };

  selectDone = event => {
    let selected = [...this.props.selected];
    let clientPhase = this.props.clientPhase;
    this.props.gameHandler.SelectDone(clientPhase, selected);
  };

  pass = event => {
    this.props.gameHandler.Pass();
  };

  selectAttackers = event => {
    let attackerAssignments = [...this.props.attackerAssignments];
    this.props.gameHandler.SelectAttackers(attackerAssignments);
  };

  selectBlockers = event => {
    let blockerAssignments = [...this.props.blockerAssignments];
    this.props.gameHandler.SelectBlockers(blockerAssignments);
  };

  render() {
    const {
      clientPhase,
      gamePhase,
      playerName,
      playerUserId,
      activePlayer,
      turnPlayer,
      upTo,
      autoPass,
      opponentName
    } = this.props;

    const gamePhaseStr = {
      0: "NOPHASE",
      1: "REDRAW",
      2: "BEGIN",
      3: "MAIN",
      4: "MAIN_RESOLVING",
      5: "END"
    }[gamePhase];

    const amIActive = activePlayer === playerUserId;
    const actPlayer = amIActive ? playerName : opponentName;
    const turPlayer = turnPlayer === playerUserId ? playerName : opponentName;

    const activeDisplay = (
      <Grid container spacing={0}>
        <Grid item xs={12}>
          <Textfit
            mode="single"
            forceSingleModeWidth={false}
            style={{ margin: "5%" }}
          >
            Phase: {gamePhaseStr}
          </Textfit>
        </Grid>
        <Grid item xs={12}>
          <Textfit
            mode="single"
            forceSingleModeWidth={false}
            style={{ margin: "5%" }}
          >
            Turn: {turPlayer}
          </Textfit>
        </Grid>
        <Grid item xs={12}>
          <Textfit
            mode="single"
            forceSingleModeWidth={false}
            style={{ margin: "5%" }}
          >
            Active: {actPlayer}
          </Textfit>
        </Grid>
      </Grid>
    );

    let buttonMenu = <div />;
    if (
      clientPhase === ClientPhase.NOT_STARTED ||
      gamePhase === proto.Phase.REDRAW
    ) {
      buttonMenu = (
        <Grid container spacing={16}>
          <Grid item xs={6}>
            <Button onClick={this.keep} disabled={!amIActive} text="Keep" />
          </Grid>
          <Grid item xs={6}>
            <Button onClick={this.redraw} disabled={!amIActive} text="Redraw" />
          </Grid>
        </Grid>
      );
    } else if (IsSelectCardPhase(clientPhase)) {
      buttonMenu = (
        <Button disabled={!upTo} onClick={this.selectDone} text="Done" />
      );
    } else if (clientPhase === ClientPhase.SELECT_ATTACKERS) {
      buttonMenu = (turnPlayer === playerUserId ) ?
        <Button onClick={this.selectAttackers} text="Attack" /> : <div/>
        ;
    } else if (clientPhase === ClientPhase.SELECT_BLOCKERS ) {
      buttonMenu = (turnPlayer !== playerUserId) ?
        <Button onClick={this.selectBlockers} text="Block" /> : <div/>;
    } else {
      buttonMenu = (
        <Button
          disabled={!amIActive || autoPass}
          onClick={this.pass}
          text="Pass"
        />
      );
    }

    return (<div>{buttonMenu}</div>);
    /*
      return (
      <Paper className="message-display">
        <Grid container spacing={0} style={{ color: "black" }}>
          <Grid item xs={12}>
            {activeDisplay}
          </Grid>
          <Grid item xs={12}>
            {buttonMenu}
          </Grid>
        </Grid>
      </Paper>
    );
      */
  }
}

export default connect(mapStateToProps)(withHandlers(ButtonDisplay));
